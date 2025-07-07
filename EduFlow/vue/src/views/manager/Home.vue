<template>
  <div>
    <div class="card" style="padding: 15px">
      Hello, {{ user?.name }}! Welcome to EduFlow Learning Management System
    </div>

    <div style="display: flex; margin: 10px 0">
      <div style="width: 50%;" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">Announcements</div>
        <div>
          <el-timeline reverse slot="reference">
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

      <!-- 饼图 pie1 隐藏 -->
      <div style="width: 50%; height: 350px" class="card" id="pie1" v-show="false"></div>
    </div>

    <div style="display: flex; margin: 10px 0">
      <!-- 饼图 pie2 隐藏 -->
      <div style="width: 50%; height: 400px" class="card" id="pie2" v-show="false"></div>

      <!-- 柱状图 bar 隐藏 -->
      <div style="width: 50%; height: 400px" class="card" id="bar" v-show="false"></div>
    </div>
  </div>
</template>

<script>
import * as echarts from "echarts";

let pie1Options = {
  title: { text: '', subtext: '', left: 'center' },
  tooltip: { trigger: 'item', formatter: '{a} <br/>{b} : {c} ({d}%)' },
  legend: { orient: 'vertical', left: 'left' },
  series: [{
    name: '',
    type: 'pie',
    radius: '50%',
    center: ['50%', '60%'],
    data: [
      { value: 1048, name: 'Mathematics' },
      { value: 735, name: 'English' },
      { value: 580, name: 'Physics' },
      { value: 484, name: 'Chemistry' },
      { value: 300, name: 'Biology' }
    ]
  }]
}

let pie2Options = {
  title: { text: '', subtext: '', left: 'center' },
  tooltip: { trigger: 'item', formatter: '{a} <br/>{b} : {c} ({d}%)' },
  legend: { orient: 'vertical', left: 'left' },
  series: [{
    name: '',
    type: 'pie',
    radius: '50%',
    center: ['50%', '60%'],
    data: [
      { value: 1048, name: 'Computer Science' },
      { value: 735, name: 'Business' },
      { value: 580, name: 'Art & Design' },
      { value: 484, name: 'Language' },
      { value: 300, name: 'Science' }
    ]
  }]
}

let barOptions = {
  title: { text: '', subtext: '', left: 'center' },
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: { type: 'value' },
  tooltip: { trigger: 'item' },
  series: [{
    data: [120, 200, 150, 80, 70, 110, 130],
    type: 'bar',
    itemStyle: {
      normal: {
        color: function () {
          return "#" + Math.floor(Math.random() * (256 * 256 * 256 - 1)).toString(16);
        }
      }
    }
  }]
}

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: []
    }
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
    this.loadEquipment()
    this.loadCourse()
    this.loadCoach()
  },
  methods: {
    loadEquipment() {
      this.$request.get('/echarts/equipmentData').then(res => {
        if (res.code === '200') {
          let chartDom = document.getElementById('pie1');
          let myChart = echarts.init(chartDom);
          pie1Options.title.text = res.data.text
          pie1Options.title.subtext = res.data.subtext
          pie1Options.series[0].name = res.data.name
          pie1Options.series[0].data = res.data.data
          myChart.setOption(pie1Options);
        }
      })
    },
    loadCourse() {
      this.$request.get('/echarts/courserData').then(res => {
        if (res.code === '200') {
          let chartDom = document.getElementById('pie2');
          let myChart = echarts.init(chartDom);
          pie2Options.title.text = res.data.text
          pie2Options.title.subtext = res.data.subtext
          pie2Options.series[0].name = res.data.name
          pie2Options.series[0].data = res.data.data
          myChart.setOption(pie2Options);
        }
      })
    },
    loadCoach() {
      this.$request.get('/echarts/coachData').then(res => {
        if (res.code === '200') {
          let chartDom = document.getElementById('bar');
          let myChart = echarts.init(chartDom);
          barOptions.title.text = res.data.text
          barOptions.title.subtext = res.data.subtext
          barOptions.xAxis.data = res.data.xAxis
          barOptions.series[0].data = res.data.yAxis
          myChart.setOption(barOptions)
        }
      })
    }
  }
}
</script>
