<template>
  <div>

    <div class="table">
      <el-table :data="tableData" stripe>

        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="课程名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="课程类型" show-overflow-tooltip></el-table-column>
        <el-table-column prop="teacherName" label="授课教师" show-overflow-tooltip></el-table-column>
        <el-table-column prop="score" label="学分" show-overflow-tooltip></el-table-column>
        <el-table-column prop="num" label="上课人数" show-overflow-tooltip></el-table-column>
        <el-table-column prop="room" label="上课教室" show-overflow-tooltip></el-table-column>
        <el-table-column prop="week" label="周几" show-overflow-tooltip></el-table-column>
        <el-table-column prop="segment" label="第几大节" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="上课状态" show-overflow-tooltip></el-table-column>
        <el-table-column prop="studentName" label="选课学生" show-overflow-tooltip></el-table-column>

        <el-table-column label="操作" width="180" align="center" v-if="user.role === 'STUDENT'">
          <template v-slot="scope">
            <el-button plain type="danger" size="mini" @click=del(scope.row.id) :disabled="scope.row.status!=='未开课'">取消选课</el-button>
            <el-button plain type="primary" size="mini" @click=initComment(scope.row) :disabled="scope.row.status!=='已结课'">评教</el-button>
          </template>
        </el-table-column>

      </el-table>

      <el-dialog title="信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
        <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="content" label="评教内容">
            <el-input type="textarea" :rows="5" v-model="form.content" autocomplete="off"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="fromVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </el-dialog>


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
  name: "Choice",
  data() {
    return {  //页面中所有变量都放在这里
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      fromVisible: false, //新增弹窗一开始是不可见的
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        content: [
          {required: true, message: '请输入内容', trigger: 'blur'},
        ]
      },
      ids: []
    }
  },
  created() {
    this.load(1)//页面创建完后调用的方法，请求显示第一页的数据
  },
  methods: {
    initComment(row){
      this.form=JSON.parse(JSON.stringify(row)) //进行深拷贝 这时form里面的数据是选中行的选课数据（对应choice表）
      this.fromVisible=true //打开评教弹窗
    },
    save(){
      let data={
        name:this.form.name,  //comment表和choice表的name都是课程名称
        teacher:this.form.teacherName, //comment表的teacher对应choice表的teacherName
        student:this.user.name,  //评教学生就是当前学生用户的名字
        content:this.form.content
      }
      this.$request.post('/comment/add',data).then(res=>{
        if(res.code==='200'){
          this.$message.success('评教成功')
        }else{
          this.$message.error(res.msg)
        }
      })
      this.fromVisible=false
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/choice/delete/' + id).then(res => { //把id传给后台 即通过访问choice/delete/id这个url调用通过id删除的方法
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
    load(pageNum) {  // 分页查询 通过输入页数来获取分页的数据
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/choice/selectPage', { //selectPage是ChoiceService里的接口
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() { //重置方法（查询旁边的按钮）清空输入框，返回第一页
      this.name = null
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
