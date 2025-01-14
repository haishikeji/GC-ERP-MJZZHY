<template>
  <!--我是单件订单列表 开始-->
  <div class="page-container list-container">
    <!--操作按钮 开始-->
    <div class="list-btns-container">
      <el-button v-permission="['purchasingRequisition:add']" type="default" size="mini" @click="handleFormPageOpen(null)"><i class="fa fa-plus" aria-hidden="true" /> 新增</el-button>
      <el-button v-permission="['purchasingRequisition:delete']" type="danger" size="mini" :disabled="selections.length === 0" @click="handleDelete"><i class="fa fa-trash" aria-hidden="true" /> 删除</el-button>
      <el-button type="success" size="mini" @click="handleRefresh"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>

      <!-- <el-upload style="display:inline" :show-file-list="false" :action="`${webHost}/general/file/upload`" :multiple="false" :on-success="uplodExcelSuccess" :headers="setHeaders">
        <el-button size="mini" type="primary" icon="el-icon-upload">导入标准件</el-button>
      </el-upload> -->

    </div>

    <!--操作按钮 结束-->
    <!--搜索框 开始-->
    <el-form class="list-search-container" size="mini" :inline="true">
      <el-form-item label="申请单号：">
        <el-input v-model.trim="query.formNo" size="mini" type="text" placeholder="申请单号" :clearable="true" style="width: 130px;" @keyup.enter.native="handleSearch" />
      </el-form-item>
      <el-form-item label="申请日期：">
        <el-date-picker v-model.trim="query.formDate" size="mini" :clearable="true" format="yyyy-MM-dd" value-format="yyyy-MM-dd" align="center" type="date" placeholder="申请日期" style="width: 130px;" @keyup.enter.native="handleSearch" @change="handleSearch" />
      </el-form-item>
      <el-form-item>
        <el-button type="success" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" icon="el-icon-delete" plain @click="handleSearch('clear')">清空</el-button>
      </el-form-item>
    </el-form>
    <!--搜索框 结束-->
    <!--列表数据 开始-->
    <div class="el-table-container">
      <div>
        <el-table ref="table" show-summary :summary-method="()=>{return []}" :data="list" size="mini" border height="100%" header-row-class-name="list-header-row" row-class-name="list-row" @selection-change="handleSelect">
          <el-table-column type="selection" width="40" align="center" />
          <el-table-column type="index" label="行号" width="40" align="center" :index="index=>$indexContinue(index,'query','pn','pageSize')" />
          <el-table-column label="申请单号" prop="formNo" width="150" align="center" />
          <el-table-column label="申请日期" prop="formDate" width="90" align="center">
            <template #default="scope">{{ $moment(scope.row.formDate).format('YYYY-MM-DD') }}</template>
          </el-table-column>
          <el-table-column label="采购类别" prop="baseMaterialCategoryName" width="90" align="center" />
          <el-table-column label="采购类型" prop="purchasingType" width="90" align="center" />
          <el-table-column label="审批通过" width="90" align="center">
            <template #default="scope">
              <span v-if="scope.row.approve == 0">否</span>
              <span v-if="scope.row.approve == 1">申请中</span>
              <span v-if="scope.row.approve == 2">审批通过</span>
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" align="center" />
          <el-table-column label="操作" width="40" align="center">
            <template #default="scope">
              <el-button v-permission="['purchasingRequisition:view']" type="primary" size="mini" icon="el-icon-view" circle @click="handleFormPageOpen(scope.row.id)" />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
    <div class="pagination-container">
      <el-pagination background :current-page="query.pn" :page-size="query.pageSize" :total="query.total" :page-sizes="$pageSizeArr" layout="total, sizes, prev, pager, next, jumper" @size-change="handlePageSizeChange" @current-change="handlePageChange" />
    </div>
    <form-page ref="FormPage" :parent="this" />
  </div>
  <!--列表 结束-->
</template>

<script>
import store from '@/store'
import purchase_apply from '@/api/biz/purchasingRequisition'
import FormPage from './Form'
import commmonKeepScrollMaxins from '@/views/common/commmonKeepScrollMaxins.js'
import { getToken } from '@/utils/auth'
export default {
  name: 'PurchaseApplyList',
  // 注册子组件
  components: {
    FormPage
  },
  mixins: [commmonKeepScrollMaxins],
  data() {
    return {
      // 列表数据源
      list: [],
      // 数据字典
      dataDictionary: store.getters.dataDictionary,
      // 查询请求携带的参数
      query: {},
      queryEmptyData: {
        // approve: 1,
        formNo: '',
        formDate: '',
        total: 0,
        pageSize: this.$defaulPageSize,
        pn: 1
      },
      // 列表中选中的行对象集合
      selections: [],
      // 日期范围
      dateRange: [],
      // 当前操作者 Id
      operatorId: store.getters.id,
      // 当前操作者 姓名/账号
      operatorName: store.getters.name ? store.getters.name : store.getters.userName,
      list_ref_arr: ['table'],
      webHost: process.env.VUE_APP_BASE_API,
      setHeaders: {
        Authorization: getToken()
      }
    }
  },
  mounted() {
    this.query = JSON.parse(JSON.stringify(this.queryEmptyData))
    this.loadData()
  },
  activated: function () {
    if (this.$route.query.tab === true) {
      this.recopyScorllInActivate()
    }
    // console.log('purchaseApply act')
    // console.log(this.$route.params)
    if (this.$route.params) {
      var param = this.$route.params.param
      if (param && param.simulation === '1') {
        this.simulationPurchaseApply(param)
      }
    }
  },
  methods: {
    /** 页面事件 start */
    // 请求数据
    async loadData() {
      // console.log('loadData')
      const res = await purchase_apply.getList(this.query)
      if (res.code === 200) {
        this.list = res.data.list
        this.query.total = res.data.total
      }
    },

    // 搜索事件
    handleSearch(type) {
      if (type && type === 'clear') {
        this.query = {
          total: 0,
          pageSize: this.$defaulPageSize,
          pn: 1
        }
      }
      this.query.pn = 1
      this.loadData()
    },

    // 打开表单对话框
    handleFormPageOpen(id) {
      this.$refs.FormPage.open(id)
    },

    // 删除
    handleDelete() {
      if (this.selections.length > 0) {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(async () => {
            const res = await purchase_apply.batchDelete(this.selections)
            if (res.code === 200) {
              this.loadData()
              this.$message.success(res.message)
            } else this.$message.error(res.message)
          })
          .catch(() => {
            this.$message.info('已取消删除')
          })
      } else {
        this.$message.warning('请至少选择一条要删除的数据!')
      }
    },

    // 列表 select change 事件
    handleSelect(selections) {
      this.selections = selections
    },
    // 刷新事件
    handleRefresh() {
      this.list = []
      this.loadData()
    },
    // 翻页事件
    handlePageChange(pn) {
      this.query.pn = pn
      this.loadData()
    },
    // 分页尺寸改变
    handlePageSizeChange(size) {
      this.query.pn = 1
      this.query.pageSize = size
      this.loadData()
    },
    // 2022-5-19由模具车间的标准件调用直接模拟采购申请函数
    // standardParts 是标准件们的id
    simulationPurchaseApply(param) {
      // console.log('list simulationPurchaseApply')
      this.$refs.FormPage.simulationPurchaseApply(param)
    },
    // 2022-4-13 参考华录的bom上传
    async uplodExcelSuccess(res, type) {
      console.log('uplodExcelSuccess')
      if (res.code === 200) {
        const fileUrl = res.data.fileUrl
        if (fileUrl === '' || fileUrl === null || fileUrl === undefined) {
          this.$message.error('文件上传失败,未能解析保存位置')
          return
        }
        var param = {
          fileUrl: fileUrl
        }
        const saveRes = await purchase_apply.importExcel(param)
        if (saveRes.code === 200) {
          this.$message.success(saveRes.message)
        } else if (saveRes.code === 1000) {
          this.$message({
            // 我要可以被换行
            dangerouslyUseHTMLString: true,
            message: saveRes.message,
            type: 'warning',
            duration: 5000
          })
        } else this.$message.error(saveRes.message)
        //
      } else this.$message.error('文件上传失败')
    }
  }
}
</script>
