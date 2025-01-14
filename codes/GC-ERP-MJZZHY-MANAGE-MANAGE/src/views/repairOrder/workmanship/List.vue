<template>
  <div class="page-container">
    <el-row :gutter="0" style="height: 34%;">
      <el-col :span="24" style="height: 100%;">
        <page_head ref="page_head" @clickRow="pageHeadClickRow" />
      </el-col>
    </el-row>
    <el-row :gutter="2" style="height: 66%;">
      <el-col :span="8" style="height: 100%;">
        <page_left ref="page_left" @clickRow="pageLeftClickRow" />
      </el-col>
      <el-col :span="16" style="height: 100%;">
        <page_right ref="page_right" />
      </el-col>
    </el-row>
  </div>
</template>

<script>
import page_head from './head.vue'
import page_left from './left.vue'
import page_right from './right.vue'

export default {
  name: 'RepairOrderDesign',
  // 注册子组件
  components: {
    page_head,
    page_left,
    page_right
  },
  data() {
    return {}
  },
  created() {},
  mounted() {
    this.init()
  },
  methods: {
    init() {
      if (this.$refs.page_head) {
        this.$refs.page_head.loadData()
      }
    },
    pageHeadClickRow(row) {
      if (this.$refs.page_left) {
        this.$refs.page_left.loadData(row.repairAssignmentDetailId, row.processStatus)
      }
    },
    pageLeftClickRow(row) {
      // console.log('pageLeftClickRow')
      if (this.$refs.page_right) {
        // this.$refs.page_right.loadData(row.repairOrderId, row.repairOrderDetailId, row.repairAssignmentDetailId, row.repairAssignmentDetailBomPartId)
        this.$refs.page_right.loadData(row)
      }
    }
  }
}
</script>
