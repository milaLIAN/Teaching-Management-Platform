<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入教师姓名" style="width: 200px" v-model="teacher"></el-input>
      <el-input placeholder="请输入评教内容" style="width: 200px; margin-left: 5px" v-model="content"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button> <!--调用了load方法-->
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe >
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="课程名称" show-overflow-tooltip width="200px"></el-table-column>
        <el-table-column prop="teacher" label="授课教师" width="100px"></el-table-column>
        <el-table-column prop="content" label="评教内容"></el-table-column>
        <el-table-column prop="time" label="评教时间" width="250px"></el-table-column>

        <el-table-column label="操作" width="180" align="center" v-if="user.role==='ADMIN'">
          <template v-slot="scope">
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination"> <!--分页组件，点击分页按钮会触发 71行设置每行显示的总数-->
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "Comment",
  data() {
    return {  //页面中所有变量都放在这里
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      teacher: null,
      content: null,
      fromVisible: false, //新增弹窗一开始是不可见的
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: []
    }
  },
  created() {
    this.load(1)  //页面创建完后调用的方法，请求显示第一页的数据
  },
  methods: {
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/comment/delete/' + id).then(res => { //把id传给后台 即通过访问comment/delete/id这个url调用通过id删除的方法
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   // ids是一个数组 这里是将复选中的所有id值赋值到数组中 如 [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/comment/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // 分页查询 通过输入页数来获取分页的数据
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/comment/selectPage', { //selectPage是CommentService里的接口
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          teacher: this.teacher,
          content: this.content
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() { //重置方法（查询旁边的按钮）清空输入框，返回第一页
      this.teacher = null
      this.content = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
