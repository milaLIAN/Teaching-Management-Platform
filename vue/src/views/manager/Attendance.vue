<template>
  <div>
    <div class="search">
      <el-select v-model="courseId" placeholder="请选择课程" style="width: 200px">
        <el-option v-for="item in courseSearchData" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button> <!--调用了load方法-->
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation" v-if="user.role === 'ADMIN'">
      <el-button type="primary" plain @click="handleAdd" >添加考勤</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column type="selection" width="55" align="center" v-if="user.role === 'TEACHER'"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="courseName" label="课程名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="teacherName" label="授课教师" show-overflow-tooltip></el-table-column>
        <el-table-column prop="studentName" label="学生姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="上课时间" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="考勤状态" show-overflow-tooltip></el-table-column>

        <el-table-column label="操作" width="180" align="center" v-if="user.role==='TEACHER'">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
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

<!--新增弹窗-->
    <el-dialog title="请假信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">

        <el-form-item prop="courseId" label="选择课程">
          <el-select v-model="form.courseId" placeholder="请选择课程" style="width: 100%" @change="getStudent">
            <el-option v-for="item in courseData" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item prop="studentId" label="选择学生">
          <el-select v-model="studentId" placeholder="请选择学生" style="width: 100%">
            <el-option v-for="item in studentData" :label="item.studentName" :value="item.studentId"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item prop="time" label="上课时间">
          <el-date-picker style="width: 100%"
                          v-model="form.time"
                          value-format="yyyy-MM-dd"
                          type="date"
                          placeholder="上课时间">
          </el-date-picker>
        </el-form-item>  <!--element官网上的时间控件代码 value-format是年月日格式  -->

        <el-form-item prop="status" label="考勤状态">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" value="正常"></el-option>
            <el-option label="迟到" value="迟到"></el-option>
            <el-option label="早退" value="早退"></el-option>
            <el-option label="缺席" value="缺席"></el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>



  </div>
</template>

<script>
export default {
  name: "Attendance",
  data() {
    return {  //页面中所有变量都放在这里
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        courseId: [
          {required: true, message: '请选择课程', trigger: 'blur'},
        ],
        time: [
          {required: true, message: '请选择日期', trigger: 'blur'},
        ]
      },
      ids: [],
      courseData:[],
      studentData:[],
      studentId:null,
      courseId:null,
      courseSearchData:[]
    }
  },
  created() {
    this.load(1)  //页面创建完后调用的方法，请求显示第一页的数据
    this.loadCourse()
    this.loadCourseSearch()
  },
  methods: {
    loadCourseSearch(){
      if('STUDENT' === this.user.role){ //先对用户的身份进行判断，如果是学生，调用choice/selectAll
        this.$request.get('/choice/selectAll', {
          params: {
            studentId:this.user.id
          }
        }).then(res => {
          if (res.code === '200') {
            res.data.forEach(item => {
              item.id = item.courseId
            })
  //res.data是一个choice的数据列表，由于搜索时要用到course的id，也就是choice的courseId，所以这里要用forEach方法将列表数据项的id值换成courseId，才能赋值给courseSearchData
            this.courseSearchData = res.data
          } else {
            this.$message.error(res.msg)
          }
        })
      }else{
        let url='ADMIN' === this.user.role ? '/course/selectAll': '/course/selectAll?teacherId=' + this.user.id
        //另一种传参查询方法，就是直接在url里加?属性=属性值
        //管理员和教师都调用course/selectAll 如果是教师，还需要传入teacherId作为筛选条件
        this.$request.get(url).then(res => {
          if (res.code === '200') {
            this.courseSearchData = res.data
          } else {
            this.$message.error(res.msg)
          }
        })
      }

    },
    loadCourse() { //教师只能新增自己任教课程的考勤情况，所以用params插入teacherId参数作为/course/selectAll的查询条件
      this.$request.get('/course/selectAll',{
        params:{
          teacherId:this.user.role==='TEACHER'?this.user.id:null}
      }).then(res => {
        if (res.code === '200') {
          this.courseData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getStudent(courseId){  //courseId作为课程选择下拉框的回调参数
      //调用选课类Choice的查询接口 传入courseId 获取选择该门课的学生信息
      this.$request.get('/choice/selectAll', {
        params: {
          courseId: courseId
        }
      }).then(res => {
        if (res.code === '200') {
          this.studentData = res.data
          this.studentId = null  //因为这里在改变课程选择后学生选择就会清空，所以不能简单地在编辑方法里调用getStudent，否则studentId会绑定不上
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {
        teacherId:this.user.id,
      }  // 点击添加考勤时，将老师的id传入
      this.fromVisible = true
      this.studentId = null //按新增键的时候，学生下拉框清0
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))
      this.getStudentEdit(this.form.courseId)
    },
    getStudentEdit(courseId){
      this.$request.get('/choice/selectAll', {
        params: {
          courseId: courseId //课程id作为查询条件传入，查询选择了当前这门课的学生
        }
      }).then(res => {
        if (res.code === '200') {
          this.studentData = res.data //是choice表中选择当前课程的学生数据
          this.studentId = this.form.studentId //这里不像getStudent一样对studentId置空 所以是可以绑定上的
          this.fromVisible = true
        } else {
          this.$message.error(res.msg)
        }
      })

    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.studentId = this.studentId //将studenId传入form.studentId
          this.$request({
            url: this.form.id ? '/attendance/update' : '/attendance/add', //判断表单有没有id，如果有就是编辑，如果没有就是新增
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form,
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
    del(id) {   // 单个删除
      this.$confirm('您确定删除该考勤记录吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/attendance/delete/' + id).then(res => { //把id传给后台 即通过访问attendance/delete/id这个url调用通过id删除的方法
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
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/attendance/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/attendance/selectPage', { //selectPage是ApplyService里的接口
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          courseId:this.courseId //传入courseId查询
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() { //重置方法（查询旁边的按钮）清空输入框，返回第一页
      this.courseId = null
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
