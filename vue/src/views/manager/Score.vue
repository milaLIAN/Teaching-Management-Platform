<template>
  <div>

    <div class="search" >
      <el-select v-model="courseId" placeholder="请选择课程" style="width: 200px" >
        <el-option v-for="item in courseData" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-select v-model="studentId" placeholder="请选择学生" style="width: 200px" v-if="user.role === 'ADMIN'">
        <el-option v-for="item in studentData2" :label="item.name" :value="item.id"></el-option>
      </el-select>
      <el-select v-model="semester" placeholder="请选择学期" style="width: 200px" v-if="user.role !== 'TEACHER'">
        <el-option v-for="item in semesters" :key="item.value" :label="item.label" :value="item.value"></el-option>
      </el-select>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button> <!--调用了load方法-->
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>


    <div class="operation"> <!--基本就是只有教师能新增、编辑，但教师和管理员都能删除  0523：改成只有管理员能操作成绩-->
      <el-button type="primary" plain @click="handleAdd" v-if="user.role === 'ADMIN'">新增</el-button>
      <el-button type="danger" plain @click="delBatch" v-if="user.role === 'ADMIN'">批量删除</el-button>
      <el-button type="primary" @click="exportScores">导出成绩单</el-button>
    </div>

    <div class="table">
      <el-table :data="formattedTableData" stripe  @selection-change="handleSelectionChange"> <!--选中复选框时调用handleSelectionChange方法-->
        <el-table-column type="selection" width="55" align="center" v-if="user.role === 'ADMIN'"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="studentName" label="学生姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="courseName" label="课程名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="teacherName" label="授课教师" show-overflow-tooltip></el-table-column>
        <el-table-column prop="semester" label="学期" show-overflow-tooltip v-if="user.role !== 'TEACHER'"></el-table-column>
        <el-table-column prop="ordinaryScore" label="平时分" show-overflow-tooltip></el-table-column>
        <el-table-column prop="examScore" label="期末分" show-overflow-tooltip></el-table-column>
        <el-table-column prop="score" label="总成绩" show-overflow-tooltip></el-table-column>

        <el-table-column label="操作" width="180" align="center" v-if="user.role === 'ADMIN'">
          <template v-slot="scope">
           <!-- <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini" v-if="user.role === 'TEACHER'">编辑</el-button>-->
            <el-button plain type="danger" size="mini" @click=del(scope.row.id) >删除</el-button>
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

        <el-form-item prop="ordinaryScore" label="平时分">
          <el-input v-model="form.ordinaryScore" autocomplete="off" ></el-input>
        </el-form-item>

        <el-form-item prop="examScore" label="期末分">
          <el-input v-model="form.examScore" autocomplete="off"></el-input>
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
import FileSaver from 'file-saver';
import * as XLSX from 'xlsx';

export default {
  name: "Score",
  data() {
    return {  //页面中所有变量都放在这里
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      courseId:null,
      semester: null, // 确保 semester 在 data 中初始化
      fromVisible: false, //新增弹窗一开始是不可见的
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        ordinaryScore: [
          {required: true, message: '请输入平时分', trigger: 'blur'},
        ],
        examScore: [
          {required: true, message: '请输入期末分', trigger: 'blur'},
        ],
      },
      ids: [],
      courseData: [],
      studentData: [],
      studentData2: [],
      semesters: [
        { value: 1, label: '大一上' },
        { value: 2, label: '大一下' },
        { value: 3, label: '大二上' },
        { value: 4, label: '大二下' },
        { value: 5, label: '大三上' },
        { value: 6, label: '大三下' },
        { value: 7, label: '大四上' },
        { value: 8, label: '大四下' }
      ],
      semesterMap: {
        1: '大一上',
        2: '大一下',
        3: '大二上',
        4: '大二下',
        5: '大三上',
        6: '大三下',
        7: '大四上',
        8: '大四下'
      },
      studentId: null
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
    this.loadCourse()
    this.loadStudent()
  },
  methods: {
    loadCourse() { //教师只能新增自己任教的课程的成绩，所以用params插入teacherId参数作为/course/selectAll的查询条件
      //0523：这里我要改成录入成绩只由管理员完成，所以不需要传入teacherID了
      this.$request.get('/course/selectAll'
      //   params:{
      //     teacherId:this.user.role==='TEACHER'?this.user.id:null}
      // }
        ).then(res => {
        if (res.code === '200') {
          this.courseData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadStudent() { //教师只能新增自己任教的课程的成绩，所以用params插入teacherId参数作为/course/selectAll的查询条件
      //0523：这里我要改成录入成绩只由管理员完成，所以不需要传入teacherID了
      this.$request.get('/student/selectAll'

      ).then(res => {
        if (res.code === '200') {
          this.studentData2 = res.data
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
          this.studentId = null
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.form = {
        teacherId:this.user.id //教师录入成绩信息的时候传入teacherId
      }
      this.fromVisible = true
      this.studentId = null //按新增键的时候，学生下拉框清0
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据 效果是将内容显示在输入框中
      this.studentId = this.form.studentId
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.studentId = this.studentId //将studenId传入form.studentId
          this.$request({
            url: this.form.id ? '/score/update' : '/score/add', //判断表单有没有id，如果有就是编辑，如果没有就是新增
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
      this.$confirm('您确定删除成绩吗？删除后需要重新录入！', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/score/delete/' + id).then(res => { //把id传给后台 即通过访问score/delete/id这个url调用通过id删除的方法
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
        this.$request.delete('/score/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/score/selectPage', { //selectPage是ScoreService里的接口
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          courseId: this.courseId,  //把courseId作为查询条件传过去
          studentId:this.studentId, //把studentId作为查询条件传过去
          semester:this.semester
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() { //重置方法（查询旁边的按钮）清空输入框，返回第一页
      this.courseId=null
      this.studentId=null
      this.semester=null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    exportScores() {
      // 处理数据，保留需要的字段并添加中文标题行
      const exportData = this.tableData.map(item => ({
        "学生姓名": item.studentName,
        "课程名称": item.courseName,
        "学期": item.semester,
        "平时分": item.ordinaryScore,
        "期末分": item.examScore,
        "总成绩": item.score
      }));

      // 转换为工作表
      const ws = XLSX.utils.json_to_sheet(exportData);
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, "成绩单");

      // 导出为 Excel 文件
      const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([wbout], { type: "application/octet-stream" });
      FileSaver.saveAs(blob, "成绩单.xlsx");
    }
  }
}
</script>

<style scoped>

</style>
