<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入作业说明" style="width: 200px" v-model="content"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button> <!--调用了load方法-->
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd" v-if="user.role==='STUDENT'">提交作业</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="content" label="作业说明" show-overflow-tooltip></el-table-column>
        <el-table-column prop="studentName" label="学生姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="courseName" label="课程名称"></el-table-column>
        <el-table-column prop="teacherName" label="授课教师"></el-table-column>

        <el-table-column prop="file" label="作业文件">
          <template v-slot="scope">
            <el-button type="warning" size="mini" @click="down(scope.row.file)">下载查看</el-button>
          </template>
        </el-table-column>

        <el-table-column prop="score" label="作业打分"></el-table-column>
        <el-table-column prop="descr" label="审核说明"></el-table-column>

        <el-table-column label="操作" width="180" align="center" v-if="user.role!=='ADMIN'">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini" v-if="user.role === 'STUDENT' && !scope.row.score">编辑</el-button>
            <el-button plain type="primary" @click="handleCheck(scope.row)" size="mini" v-if="user.role === 'TEACHER'">评改</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)  v-if="user.role === 'STUDENT'">删除</el-button>
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

<!--新增弹窗（按提交作业）-->
    <el-dialog title="作业信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">

        <el-form-item prop="content" label="作业说明">
          <el-input type="textarea" :rows="5" v-model="form.content" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item prop="courseId" label="选择课程">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option v-for="item in courseData" :label="item.name" :value="item.courseId"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="作业文件">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="text"
              :on-success="handleFileSuccess"
          >
            <el-button type="primary">上传文件</el-button>
          </el-upload>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="作业评改" :visible.sync="checkVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules">
        <el-form-item prop="score" label="打分">
          <el-input  v-model="form.score" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="descr" label="打分说明">
          <el-input type="textarea" :rows="4" v-model="form.descr" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="checkVisible = false">取 消</el-button>
        <el-button type="primary" @click="check">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
export default {
  name: "Homework",
  data() {
    return {  //页面中所有变量都放在这里
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      status: null,
      content:null,
      fromVisible: false,
      checkVisible: false, //新建一个审核弹窗的可见变量
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        content: [
          {required: true, message: '请输入内容', trigger: 'blur'},
        ],
        courseId: [
          {required: true, message: '请选择课程', trigger: 'blur'},
        ],
        score: [
          {required: true, message: '请输入分数', trigger: 'blur'},
        ]
      },
      ids: [],
      courseData:[]
    }
  },
  created() {
    this.load(1)  //页面创建完后调用的方法，请求显示第一页的数据
    this.loadCourse()
  },
  methods: {
    loadCourse() { //学生只能看到自己选的课
      this.$request.get('/choice/selectAll',{
        params:{
          studentId:this.user.role==='STUDENT'?this.user.id:null
        }
      }).then(res => {
        if (res.code === '200') {
          this.courseData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {
        studentId:this.user.id,
        status: '待审核'
      }  // 在点击请假申请按钮时，传入当前学生用户的id作为form表单（这个表单是要传给后台作数据库插入的）的studenId值
      //同时新增申请的状态默认是待审核
      this.fromVisible = true
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))
      this.form.status='待审核'
      this.form.descr=''
      this.fromVisible = true   // 打开弹窗
    },
    handleCheck(row){
      this.form = JSON.parse(JSON.stringify(row))
      this.checkVisible=true
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/homework/update' : '/homework/add', //判断表单有没有id，如果有就是编辑，如果没有就是新增
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    check(){
      this.$request.put('homework/update',this.form).then(res=>{
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.load(1) //刷新一下
          this.checkVisible = false //关闭当前弹窗
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('你确定删除作业记录吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/homework/delete/' + id).then(res => { //把id传给后台 即通过访问homework/delete/id这个url调用通过id删除的方法
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
      this.$request.get('/homework/selectPage', { //selectPage是HomeworkService里的接口
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          content: this.content, //传入courseId进行搜索
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() { //重置方法（查询旁边的按钮）清空输入框，返回第一页
      this.status = null
      this.content= null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleFileSuccess(res){
      this.form.file = res.data
    },
    down(url){
      location.href=url
    },
  }
}
</script>

<style scoped>

</style>
