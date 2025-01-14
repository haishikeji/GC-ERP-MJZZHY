<template>
  <el-drawer title="订单表单" :visible.sync="visible" :modal-append-to-body="false" :modal="false" direction="rtl" size="100%" @opened="opened">
    <div class="page-container form-container">
      <!-- 功能按钮 -->
      <div class="form-btns-container">
        <span class="title-label"><i class="el-icon-document" /> 维修表单</span>
        <el-button v-if="(formData.id !== '0' && formData.id !== null)" v-permission="['repairOrder:edit']" :type="editStatus ? 'warning' : 'primary'" size="mini" @click="handleEdit"><i class="fa fa-pencil-square-o" aria-hidden="true" /> {{ editStatus ? '取消编辑' : '编辑' }}</el-button>
        <el-button v-if="editStatus" type="default" size="mini" @click="handleSave"><i class="fa fa-floppy-o" aria-hidden="true" /> 保存</el-button>
        <div class="screen-btn" @click="handleScreen">
          <template v-if="!isFullscreen">
            <i class="fa fa-window-maximize" aria-hidden="true" />
            <!-- <span>全屏</span> -->
          </template>
          <template v-else>
            <i class="fa fa-window-restore" aria-hidden="true" />
            <!-- <span>还原</span> -->
          </template>
        </div>
        <div class="close-btn" @click="handleClose">
          <i class="fa fa-times" aria-hidden="true" />
          <!-- <span>关闭</span> -->
        </div>
      </div>
      <!-- 主表 -->
      <el-form ref="form" class="master-container" :model="formData" :rules="formDataRules" size="mini" label-width="100px" :show-message="false">
        <el-row :gutter="30">
          <el-col :span="6">
            <el-form-item label="订单号：">
              <el-input v-if="editStatus" v-model.trim="formData.formNo" size="mini" type="text" placeholder="订单号" :clearable="true" />
              <span v-else>{{ formData.formNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="产品番号：">
              <el-input v-if="editStatus" v-model.trim="formData.title" size="mini" type="text" placeholder="产品番号" :clearable="true" />
              <span v-else>{{ formData.title }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="下单日期：">
              <el-date-picker v-if="editStatus" v-model.trim="formData.formDate" size="mini" :clearable="false" format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" align="center" type="date" placeholder="下单日期" />
              <span v-else>{{ formData.formDate }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="纳期：">
              <el-date-picker v-if="editStatus" v-model.trim="formData.deadline" size="mini" :clearable="false" format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" align="center" type="date" placeholder="纳期" />
              <span v-else>{{ formData.deadline }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="接收人员：">
              <el-input v-if="editStatus" v-model.trim="formData.employeeName" size="mini" type="text" placeholder="我司接收人员" readonly :clearable="false">
                <el-button slot="append" icon="el-icon-search" @click="() => employeeChoiceView()" />
              </el-input>
              <span v-else>{{ formData.employeeName }} </span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="客户简称：" required>
              <el-input v-if="editStatus" v-model.trim="formData.customerName" size="mini" type="text" placeholder="客户简称" readonly :clearable="false">
                <el-button slot="append" icon="el-icon-search" @click="() => customerChoiceView()" />
              </el-input>
              <span v-else>{{ formData.customerName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注：">
              <el-input v-if="editStatus" v-model.trim="formData.remark" size="mini" type="text" placeholder="备注" :clearable="true" />
              <span v-else>{{ formData.remark }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!-- 从表 -->
      <div class="details-container">
        <el-row :gutter="2">
          <el-col :span="24">
            <div class="details-head">
              <div class="title"><i class="fa fa-th-list" aria-hidden="true" /> 产品明细</div>
              <div v-if="editStatus" class="details-btns-container">
                <el-button type="primary" size="mini" @click="handleAddProduct"><i class="fa fa-plus" aria-hidden="true" /> 添加</el-button>
                <el-input-number v-model="addProductNum" size="mini" :min="1" controls-position="right" style="width: 60px;" />
                <div style="display: inline-block; padding-top: 5px; padding-right: 10px; padding-left: 2px; font-size: 12px;">条</div>
                <el-button type="danger" size="mini" :disabled="formData.products && formData.products.length === 0" @click="handleBatchDeleteProducts"><i class="el-icon-delete" aria-hidden="true" /> 全部删除</el-button>
              </div>
            </div>
            <div class="details-body">
              <el-table ref="productsTable" :data="formData.products" size="mini" height="100%" border header-row-class-name="list-header-row" highlight-current-row @current-change="handleProductChange">
                <el-table-column v-if="editStatus" key="0" label="操作" width="47" align="center">
                  <template #default="scope">
                    <div>
                      <el-button icon="el-icon-close" size="mini" circle type="danger" @click="handleDelProduct(scope.row,scope.$index)" />
                    </div>
                  </template>
                </el-table-column>
                <el-table-column type="index" label="行号" width="47" align="center" />
                <el-table-column label="维修原因" width="100" align="center">
                  <template #default="scope">
                    <el-select v-if="editStatus" v-model="scope.row.dataDictionaryId" size="mini" placeholder="请选择">
                      <el-option v-for="item in repairReason" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <span v-else>{{ scope.row.repairReason }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="维修要求" width="200" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.requirement" size="mini" type="text" placeholder="维修要求" :clearable="true" />
                    </div>
                    <div v-else>{{ scope.row.requirement }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="维修金额" width="100" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input-number v-model.trim="scope.row.amount" size="mini" placeholder="维修金额" :clearable="true" :min="0" :precision="2" controls-position="right" />
                    </div>
                    <div v-else>{{ scope.row.amount }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="备注" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.remark" size="mini" type="text" placeholder="备注" :clearable="true" />
                    </div>
                    <div v-else>{{ scope.row.remark }}</div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="dialog-foot">
        <span>制单人：{{ formData.creatorName }}</span>
        <span>制单时间：{{ $moment(formData.createTime).format('YYYY-MM-DD HH:mm:ss') }}</span>
      </div>
      <dialog-customer-choice ref="customerSingleChoice" :multiple="false" :parent="this" :width="'800px'" :simple-selected="customerSimpleChoice" />
      <dialog-employee-choice ref="employeeSingleChoice" department-name="技术研发中心" :multiple="false" :parent="this" :width="'800px'" :simple-selected="employeeSimpleChoice" />
    </div>
  </el-drawer>
</template>

<script>
import store from '@/store'
import { deepClone } from '@/utils'
import { getToken } from '@/utils/auth'
import validate from '@/utils/validate'
// import quotation_api from '@/api/biz/quotation'
import repair_order_api from '@/api/biz/repairOrder'
import DialogCustomerChoice from '@/views/dialogs/DialogCustomerChoice.vue'
import DialogEmployeeChoice from '@/views/dialogs/DialogEmployeeByDepartmentChoice.vue'
export default {
  name: 'RepairOrderForm',
  components: {
    DialogCustomerChoice,
    DialogEmployeeChoice
  },
  props: {
    parent: {
      type: Object,
      default: () => null
    }
  },
  data() {
    return {
      webHost: process.env.VUE_APP_BASE_API,
      isFullscreen: false,
      visible: false,
      editStatus: false,
      formData: {},
      // 主表单数据结构
      formEmptyData: {
        id: null,
        formNo: '',
        formDate: this.$moment(new Date()).format('YYYY-MM-DD HH:mm:ss'),
        title: '',
        customerId: '0',
        customerName: '',
        employeeId: '1531076688912670721',
        employeeName: '吕晶',
        amount: '0.00',
        remark: '',
        products: [],

        creatorName: store.getters.name ? store.getters.name : ''
      },
      // 当前form表单，是否全屏显示
      productEmptyData: {
        id: null,
        saleOrderId: '0',
        inquiryDetailId: '0',
        quotationDetailId: '0',
        contractDetailId: '0',
        productCode: '',
        productName: '',
        baseMaterialId: '',
        baseMaterialName: '',
        number: 1,
        unit: '套',
        remark: '',
        details: []
      },
      // 主表校验规则
      formDataRules: {
        customerId: [{ required: true, trigger: 'blur', validator: validate.isEmpty }]
      },
      /** options 数组区 */
      addProductNum: 1,
      setHeaders: {
        Authorization: getToken()
      },
      currentProduct: null,
      // 维修i原因
      repairReason: []
    }
  },
  created() {
    this.formData = deepClone(this.formEmptyData)
  },
  mounted() {},
  methods: {
    open(id) {
      id = id || '0'
      this.formData = deepClone(this.formEmptyData)
      this.currentProduct = null
      this.loadData(id)
    },
    async loadData(id) {
      // console.log('loaddata')
      const res = await repair_order_api.getById(id)
      this.repairReason = res.data.repairReason
      if (res.data.formData) {
        this.formData = res.data.formData
      } else {
        this.editStatus = true
      }
      this.visible = true
    },
    /**
     * 窗口打开动画完成
     */
    opened() {},

    /**
     * 全屏缩放
     */
    handleScreen() {
      const dom = document.querySelector('.list-container > .el-drawer__wrapper')
      this.isFullscreen = !this.isFullscreen
      dom.style.position = this.isFullscreen ? 'fixed' : 'absolute'
    },

    /**
     * 关闭
     */
    handleClose() {
      this.editStatus = false
      this.visible = false
    },

    /**
     * 编辑
     */
    handleEdit() {
      this.editStatus = !this.editStatus
      if (!this.editStatus) {
        this.loadData(this.formData.id)
      }
    },

    /**
     * 保存
     */
    async handleSave() {
      if (this.formData.customerName === '' || this.formData.customerId === '0') {
        this.$message.warning('客户不能为空')
        return
      }
      if (this.formData.products.length === 0) {
        this.$message.warning('至少添加一条产品信息')
        return
      }
      const res = await repair_order_api.save(this.formData)
      if (res.code === 200) {
        this.$message.success(res.message)
        this.loadData(res.data.id)
        this.editStatus = false
        this.parent.loadData()
      } else this.$message.error(res.message)
    },

    /**
     * 批量删除产品
     */
    handleBatchDeleteProducts() {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          this.formData.products = []
          this.currentProduct = null
        })
        .catch(() => {
          this.$message.info('已取消删除')
        })
    },
    // 删除产品信息
    handleDelProduct(row, index) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(async() => {
          this.formData.products.splice(index, 1)
          if (this.formData.products.length > 0) {
            this.handleProductChange(this.formData.products[0])
          } else {
            this.currentProduct = null
          }
        })
        .catch(() => {
          this.$message.info('已取消删除')
        })
    },

    // 添加从表(产品) 信息
    handleAddProduct() {
      // console.log('维修内容明细')
      const addProductNum = this.addProductNum
      for (var i = 0; i < addProductNum; ++i) {
        // 此处必须进行深拷贝，否则添加的明细，将都指向了一个对象
        const newProduct = deepClone(this.productEmptyData)
        this.formData.products.push(newProduct)
      }
    },
    handleProductChange(row) {
      if (row) {
        this.currentProduct = row
      } else {
        if (this.formData.products && this.formData.products.length > 0) {
          this.$refs.productsTable.setCurrentRow(this.formData.products[0])
        } else {
          this.currentProduct = null
        }
      }
    },
    /*
     * 客户选择对话框
     */
    customerChoiceView() {
      this.$refs.customerSingleChoice.open()
    },
    customerSimpleChoice(info) {
      this.formData.customerId = info.id
      this.formData.customerName = info.shortName
      this.$refs.customerSingleChoice.close()
    },

    /**
     * 员工选择对话框
     */
    employeeChoiceView() {
      this.$refs.employeeSingleChoice.open()
    },
    employeeSimpleChoice(info) {
      this.formData.employeeId = info.id
      this.formData.employeeName = info.name
      this.$refs.employeeSingleChoice.close()
    }
  }
}
</script>
