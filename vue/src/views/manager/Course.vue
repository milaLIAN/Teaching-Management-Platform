<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入课程名称" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button> <!--调用了load方法-->
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation" v-if="user.role === 'ADMIN'" >
      <el-button type="primary" plain @click="handleAdd" >新增</el-button>
      <el-button type="danger" plain @click="delBatch" >批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="formattedTableData" stripe  @selection-change="handleSelectionChange"> <!--选中复选框时调用handleSelectionChange方法-->
        <el-table-column type="selection" width="55" align="center" v-if="user.role === 'ADMIN'"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="name" label="课程名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="课程类型" show-overflow-tooltip></el-table-column>
        <el-table-column prop="teacherName" label="授课教师" show-overflow-tooltip></el-table-column>
        <el-table-column prop="semester" label="学期" show-overflow-tooltip></el-table-column>
        <el-table-column prop="score" label="学分" show-overflow-tooltip></el-table-column>
        <el-table-column prop="num" label="上课人数" show-overflow-tooltip></el-table-column>
        <el-table-column prop="room" label="上课教室" show-overflow-tooltip></el-table-column>
        <el-table-column prop="week" label="周几" show-overflow-tooltip></el-table-column>
        <el-table-column prop="segment" label="第几大节" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="上课状态" show-overflow-tooltip></el-table-column>

        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini" v-if="user.role === 'ADMIN'">编辑</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id) v-if="user.role === 'ADMIN'">删除</el-button>
            <el-button plain type="danger" size="mini" @click="choicecourse(scope.row)" v-if="user.role === 'STUDENT'" :disabled="scope.row.status!=='未开课'">选课</el-button>
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

<!--新增/编辑弹窗-->
    <el-dialog title="信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="name" label="课程名称">
          <el-input v-model="form.name" autocomplete="off" :disabled="user.role !== 'ADMIN'"></el-input>
        </el-form-item>
        <el-form-item prop="type" label="课程类型">
          <el-select v-model="form.type" placeholder="请选择类型" :disabled="user.role !== 'ADMIN'" style="width: 100%" >
            <el-option label="必修" value="必修"></el-option>
            <el-option label="选修" value="选修"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="teacherId" label="授课教师">
          <el-select v-model="form.teacherId" placeholder="请选择教师" style="width: 100%" :disabled="user.role !== 'ADMIN'">
            <el-option v-for="item in teacherData" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="score" label="学分">
          <el-input v-model="form.score" autocomplete="off" :disabled="user.role !== 'ADMIN'"></el-input>
        </el-form-item>
        <el-form-item prop="num" label="上课人数">
          <el-input v-model="form.num" autocomplete="off" :disabled="user.role !== 'ADMIN'"></el-input>
        </el-form-item>
        <el-form-item prop="room" label="上课教室">
          <el-input v-model="form.room" autocomplete="off" :disabled="user.role !== 'ADMIN'"></el-input>
        </el-form-item>
        <el-form-item prop="semester" label="学期">
          <el-select v-model="form.semester" placeholder="请选择类型" style="width: 100%" :disabled="user.role !== 'ADMIN'">
            <el-option label="大一(上)" value="1"></el-option>
            <el-option label="大一(下)" value="2"></el-option>
            <el-option label="大二(上)" value="3"></el-option>
            <el-option label="大二(下)" value="4"></el-option>
            <el-option label="大三(上)" value="5"></el-option>
            <el-option label="大三(下)" value="6"></el-option>
            <el-option label="大四(上)" value="7"></el-option>
            <el-option label="大四(下)" value="8"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="week" label="周几">
          <el-select v-model="form.week" placeholder="请选择类型" style="width: 100%" :disabled="user.role !== 'ADMIN'">
            <el-option label="星期一" value="星期一"></el-option>
            <el-option label="星期二" value="星期二"></el-option>
            <el-option label="星期三" value="星期三"></el-option>
            <el-option label="星期四" value="星期四"></el-option>
            <el-option label="星期五" value="星期五"></el-option>
            <el-option label="星期六" value="星期六"></el-option>
            <el-option label="星期日" value="星期日"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="segment" label="第几大节">
          <el-select v-model="form.segment" placeholder="请选择类型" style="width: 100%" :disabled="user.role !== 'ADMIN'">
            <el-option label="第一大节（08:00 ~ 09:40）" value="第一大节（08:00 ~ 09:40）"></el-option>
            <el-option label="第二大节（10:10 ~ 11:50）" value="第二大节（10:10 ~ 11:50）"></el-option>
            <el-option label="第三大节（14:20 ~ 16:00）" value="第三大节（14:20 ~ 16:00）"></el-option>
            <el-option label="第四大节（16:30 ~ 18:10）" value="第四大节（16:30 ~ 18:10）"></el-option>
            <el-option label="第五大节（19:00 ~ 20:40）" value="第五大节（19:00 ~ 20:40）"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="status" label="上课状态">
          <el-select v-model="form.status" placeholder="请选择类型" style="width: 100%" :disabled="user.role === 'STUDENT'">
            <el-option label="未开课" value="未开课"></el-option>
            <el-option label="已开课" value="已开课"></el-option>
            <el-option label="已结课" value="已结课"></el-option>
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
  name: "Course",
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
        name: [
          {required: true, message: '请输入课程名称', trigger: 'blur'},
        ],
        score: [
          {required: true, message: '请输入学分', trigger: 'blur'},
        ]
      },
      ids: [],
      teacherData:[],
      semesterMap: {
        1: '大一上',
        2: '大一下',
        3: '大二上',
        4: '大二下',
        5: '大三上',
        6: '大三下',
        7: '大四上',
        8: '大四下'
      }
    }
  },
  computed: {
    formattedTableData() {
      return this.tableData.map(item => {
        return {
          ...item,
          semester: this.semesterMap[item.semester] || item.semester
        };
      });
    }
  },
  created() {
    this.load(1)//页面创建完后调用的方法，请求显示第一页的数据
    this.loadTeacher()
  },
  methods: {
    choicecourse(row){
      let data={
        studentId:this.user.id,
        teacherId:row.teacherId,
        courseId:row.id
      }
      this.$request.post('choice/add',data).then(res=>{
        if(res.code==='200'){
          this.$message.success('选课成功')
        }else{
          this.$message.error(res.msg)
        }
      })
    },
    loadTeacher(){
      this.$request.get('/teacher/selectAll').then(res=>{
        if(res.code==='200'){
          this.teacherData=res.data
        }else{
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 将弹窗的fromVisible属性设为true 即可见
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据 效果是将内容显示在输入框中
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/course/update' : '/course/add', //判断表单有没有id，如果有就是编辑，如果没有就是新增
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
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/course/delete/' + id).then(res => { //把id传给后台 即通过访问course/delete/id这个url调用通过id删除的方法
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
        this.$request.delete('/course/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/course/selectPage', { //selectPage是CourseService里的接口
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
