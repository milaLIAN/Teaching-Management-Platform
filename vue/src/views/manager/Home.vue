<template>
  <div>
    <div class="card" style="padding: 15px">
      您好，{{ user?.name }}！欢迎使用本系统
    </div>

    <div style="display: flex; margin: 10px 0">
      <div style="width: 50%;" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">教务通知</div>
        <div >
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <div style="width: 50%;" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">考试安排</div>
        <div >
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in examplans" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.name }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>


    </div>
    <div style="display: flex" v-if="user.role === 'ADMIN'">
      <div class="card" id="pie" style="height: 400px; width: 100%"></div>
    </div>



  </div>
</template>

<script>
import * as echarts from "echarts";
let pieOptions = {
  title: {
    text: '', // 主标题
    subtext: '', // 副标题
    left: 'center'
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '', // 鼠标移上去显示内容
      type: 'pie',
      radius: '50%',
      center: ['50%', '60%'],
      data: [
        {value: 1048, name: '瑞幸咖啡'}, // 示例数据：name表示维度，value表示对应的值
        {value: 735, name: '雀巢咖啡'},
        {value: 580, name: '星巴克咖啡'},
        {value: 484, name: '栖巢咖啡'},
        {value: 300, name: '小武哥咖啡'}
      ]
    }
  ]
}


export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      examplans:[] //新增examplans
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || [] //页面创建好后，取出数据库notice表里的所有数据，返回到notices中
    }) //上面<el-timeline-item v-for="item in notices"......就是显示出notices的部分
    this.$request.get('/examplan/selectAll').then(res=>{
      if(res.code==='200'){
        this.examplans=res.data || []  //和上面一样，调用examplan/selectAll接口，将数据库中的考试安排数据返回给examplans
      }else{
        this.$message.error(res.msg) //对状态码进行判断，如果出错就进行提示 （保险做法
      }
    })
    if(this.user.role==='ADMIN'){this.getPie()}


  },
  methods : {
    getPie() {
      this.$request.get('/attendance/getPie').then(res => {  //去后台定义一个这样的接口来返回数据
        if (res.code === '200') {
          let chartDom = document.getElementById('pie');
          let myChart = echarts.init(chartDom);  //在id为pie的位置初始化图表 myChart就是具体会现实的图表

          //再通过pieOptions（就是上面定义过的统计图的数据结构）渲染从后台拿到的数据（res.data）
          pieOptions.title.text = res.data.text
          pieOptions.title.subtext = res.data.subtext
          pieOptions.series.name = res.data.name
          pieOptions.series[0].data = res.data.data

          //绑定到myChart上
          myChart.setOption(pieOptions);
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }

}
</script>
