<template>
  <div>
    <div class="search-bar" v-if="user.role !== 'STUDENT'">
      <div class="left">
        <el-select v-model="courseId" placeholder="请选择课程" style="width: 200px">
          <el-option v-for="item in courseData" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <el-button type="info" plain style="margin-left: 10px" @click="load()">查询</el-button> <!--调用了load方法-->
        <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      </div>
      <div class="right" v-if="user.role === 'ADMIN'">
        <el-select v-model="specialityId" placeholder="请选择专业" style="width: 200px" @change="loadClass">
          <el-option v-for="item in specialityData" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <el-select v-model="classId" placeholder="请选择班级" style="width: 200px">
          <el-option v-for="item in classData" :key="item.id" :label="item.name" :value="item.id"></el-option>
        </el-select>
        <el-button type="info" plain style="margin-left: 10px" @click="loadGpaRanking">查询绩点排名</el-button>
        <el-button type="warning" plain style="margin-left: 10px" @click="reset2">重置</el-button>
      </div>
    </div>


    <div class="table" v-if="user.role === 'ADMIN'">
      <div id="average-score-chart" style="width: 100%; height: 400px;"  ></div>

      <el-table :data="distributionData" v-if="distributionData.length > 0">
        <el-table-column prop="className" label="班级" width="180"></el-table-column>
        <el-table-column prop="excellent" label="优秀(90分以上)" width="180"></el-table-column>
        <el-table-column prop="good" label="良好(80-90分)" width="180"></el-table-column>
        <el-table-column prop="average" label="中等(70-80分)" width="180"></el-table-column>
        <el-table-column prop="pass" label="及格(60-70分)" width="180"></el-table-column>
        <el-table-column prop="fail" label="不及格(60分以下)" width="180"></el-table-column>
      </el-table>
      <div v-else>
        <p>暂无数据</p>
      </div>

    </div>

    <!-- GPA排名图 -->
    <div class="table" v-if="gpaRankingData.length > 0 && user.role !== 'STUDENT' ">
      <table>
        <thead>
        <tr>
          <th>排名</th>
          <th>学生姓名</th>
          <th>GPA</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="(item, index) in gpaRankingData" :key="item.studentId">
          <td>{{ index + 1 }}</td>
          <td>{{ item.studentName }}</td>
          <td>{{ item.gpa }}</td>
        </tr>
        </tbody>
      </table>
    </div>
    <div class="table" v-else v-if="user.role !== 'STUDENT'">
      <p>暂无数据</p>
    </div>

    <div v-if="user.role === 'STUDENT'" class="student-info">
      <h2>我的绩点：{{ myGpa }}</h2>
      <h3>专业排名：{{ myRankInSpeciality }}</h3>
    </div>

    <div class="table" v-if="user.role === 'STUDENT'">
      <div id="student-score-distribution-chart" style="width: 100%; height: 400px;"></div>
    </div>



    <div class="table" v-if="user.role === 'TEACHER'">
      <div id="teacher-distribution-line-chart" style="width: 100%; height: 400px;"></div>
      <div v-if="teacherDistributionData.length === 0 && user.role === 'TEACHER'" >
        <p>暂无数据</p>
      </div>
    </div>


  </div>
</template>

<script>
import * as echarts from 'echarts';
export default {
  name: "Analysis",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ids: [],
      courseData: [],
      courseId: 1,
      distributionData: [] ,// 用于存储各班成绩分布数据
      studentDistributionData: [], // 用于存储当前学生成绩分布数据
      teacherDistributionData: [], // 用于存储教师查看的学生成绩分布数据

      specialityData: [], // 新增的专业数据
      classData: [], // 新增的班级数据
      specialityId: 1, // 默认专业ID
      classId: null,
      gpaRankingData: [], // 用于存储学生绩点排名数据

      myGpa:null,
      myRankInSpeciality:null,
    }
  },
  created() {
    this.load(1)
    this.loadCourse()
    this.loadAverageScores()
    this.loadDistributionData()
    if (this.user.role === 'STUDENT') {
      this.loadStudentDistributionData();
      this.loadStudentGpa(this.user.id)
    }
    if (this.user.role === 'TEACHER') {
      this.loadTeacherDistributionData();
      this.getclassid(this.user.id, this.loadGpaRanking)
    }
    this.loadSpeciality()
    this.loadClass()
    if (this.user.role === 'ADMIN') {
      this.loadGpaRanking()
    }

  },
  methods: {
    loadStudentGpa(studentId){
      this.$request.get('/student/gpaStudent', {
        params: {
          studentId: studentId
        }
      }).then(res => {
        if (res.code === '200') {
          this.myGpa = res.data.gpa;
          this.myRankInSpeciality=res.data.rankInSpeciality;
        } else {
          this.$message.error(res.msg);
        }
      });

    },
    getclassid(teacherId, callback){
      this.$request.get('/classes/selectByTeacher', {
        params: {
          teacherId: teacherId
        }
      }).then(res => {
        if (res.code === '200') {
          this.classId = res.data.id;
          if (callback) callback(); // 调用回调函数
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadCourse() {
      this.$request.get('/course/selectAll').then(res => {
        if (res.code === '200') {
          this.courseData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadSpeciality() {
      this.$request.get('/speciality/selectAll').then(res => {
        if (res.code === '200') {
          this.specialityData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadClass() {
      this.$request.get('/classes/selectBySpeciality', {
        params: {
          specialityId: this.specialityId
        }
      }).then(res => {
        if (res.code === '200') {
          this.classData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadGpaRanking() {
      this.$request.get('/student/gpaRanking', {
        params: {
          specialityId: this.specialityId,
          classId: this.classId // 传递 classId，允许为 null
        }
      }).then(res => {
        if (res.code === '200') {
          this.gpaRankingData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },

    loadTeacherDistributionData() {
      if (this.user.role !== 'TEACHER') return;
      this.$request.get('/score/distribution-by-teacher-course', {
        params: {
          courseId: this.courseId
        }
      }).then(res => {
        if (res.code === '200' && res.data.length > 0) {
          this.teacherDistributionData = res.data;
          this.renderTeacherLineChart(res.data);
        } else {
          this.teacherDistributionData = [];
          this.renderTeacherLineChart([]);
          this.$message.error(res.msg || '暂无数据');
        }
      });
    },
    renderTeacherLineChart(data) {
      const chart = echarts.init(document.getElementById('teacher-distribution-line-chart'));

      if (!data || data.length === 0) {
        // 如果数据为空，则显示一条消息
        chart.clear();
        chart.setOption({
          title: {
            text: '学生成绩分布图'
          },
          xAxis: {
            type: 'category',
            data: []
          },
          yAxis: {
            type: 'value'
          },
          series: []
        });
        return;
      }

      // 过滤掉 null 或 undefined 的元素
      const validData = data.filter(item => item !== null && item !== undefined);

      // 将成绩分布数据转换为折线图所需的数据
      const scoreCategories = ['优秀(90分以上)', '良好(80-90分)', '中等(70-80分)', '及格(60-70分)', '不及格(60分以下)'];
      const scores = [
        validData.reduce((acc, item) => acc + (item.excellent || 0), 0),
        validData.reduce((acc, item) => acc + (item.good || 0), 0),
        validData.reduce((acc, item) => acc + (item.average || 0), 0),
        validData.reduce((acc, item) => acc + (item.pass || 0), 0),
        validData.reduce((acc, item) => acc + (item.fail || 0), 0)
      ];

      // 绘制折线图
      const option = {
        title: {
          text: '学生成绩分布图'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: scoreCategories
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '学生数量',
          type: 'line',
          data: scores
        }]
      };
      chart.setOption(option);
    },
    loadAverageScores() {
      if (this.user.role !== 'ADMIN') return;
      this.$request.get('/score/average-by-class', {
        params: {
          courseId: this.courseId
        }
      }).then(res => {
        if (res.code === '200') {
          res.data.sort((a, b) => a.class_name.localeCompare(b.class_name));
          this.renderChart(res.data);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadDistributionData() {
      if (this.user.role !== 'ADMIN') return;
      this.$request.get('/score/distribution-by-class', {
        params: {
          courseId: this.courseId
        }
      }).then(res => {
        if (res.code === '200') {
          this.distributionData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    loadStudentDistributionData() {
      this.$request.get('/score/distribution-by-student').then(res => {
        if (res.code === '200') {
          const data = res.data[0];  // 假设返回的data是 [{average: 0, fail: 0, excellent: 0, pass: 0, good: 1}]
          this.studentDistributionData = data;
          this.$nextTick(() => {
            this.renderStudentChart(data);
          });
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    renderChart(data) {
      const chart = echarts.init(document.getElementById('average-score-chart'));
      const option = {
        title: {
          text: '各班级平均分折线图'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.class_name)
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '平均分',
          type: 'line',
          data: data.map(item => item.avg_score)
        }]
      };
      chart.setOption(option);
    },
    renderStudentChart(data) {
      const chart = echarts.init(document.getElementById('student-score-distribution-chart'));
      const option = {
        title: {
          text: '个人成绩分布图'
        },
        tooltip: {
          trigger: 'item'
        },
        series: [{
          name: '成绩分布',
          type: 'pie',
          radius: '50%',
          data: [
            { value: data.excellent, name: '优秀(90分以上)' },
            { value: data.good, name: '良好(80-90分)' },
            { value: data.average, name: '中等(70-80分)' },
            { value: data.pass, name: '及格(60-70分)' },
            { value: data.fail, name: '不及格(60分以下)' }
          ]
        }]
      };
      chart.setOption(option);
    },
    load() {
      this.loadAverageScores();
      this.loadDistributionData(); // 查询按钮点击后加载对应课程的成绩分布数据
      this.loadTeacherDistributionData();
    },
    reset() {
      this.courseId = 1;
      this.loadAverageScores();
      this.loadDistributionData(); // 重新加载默认的成绩分布数据
      this.loadTeacherDistributionData();
    },
    reset2() {
      this.specialityId= 1;
      this.classId=null;
      this.loadGpaRanking()
    }
  }
}
</script>

<style scoped>
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.left, .right {
  display: flex;
  align-items: center;
}

.table {
  margin-top: 20px; /* 可以根据需要调整表格与其他内容之间的间距 */
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ddd; /* 添加边框样式 */
  padding: 8px; /* 设置单元格内边距 */
  text-align: center; /* 让内容居中显示 */
}
th {
  background-color: #f2f2f2; /* 设置表头背景色 */
  font-size: 16px; /* 调整表头字体大小 */
}

.student-info {
  background-color: #f0f0f0;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.student-info h2 {
  font-size: 24px;
  color: #333;
}

.student-info h3 {
  font-size: 20px;
  color: #666;
}
</style>
