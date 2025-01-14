<template>
  <!--单件订单-->
  <el-drawer title="采购申请表单" :visible.sync="visible" :modal-append-to-body="false" :modal="false" direction="rtl"
    size="100%" @opened="opened">
    <div class="page-container form-container">
      <!-- 功能按钮 -->
      <div class="form-btns-container">
        <span class="title-label"><i class="el-icon-document" /> 表单</span>
        <el-button v-if="(formData.id !== '0' && formData.id !== null)" v-permission="['ticketReversal:edit']"
          :type="editStatus ? 'warning' : 'primary'" size="mini" @click="handleEdit"><i class="fa fa-pencil-square-o"
            aria-hidden="true" /> {{ editStatus ? '取消编辑' : '编辑' }}</el-button>
        <el-button v-if="editStatus" type="default" size="mini" @click="handleSave"><i class="fa fa-floppy-o"
            aria-hidden="true" /> 保存</el-button>
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
      <el-form ref="form" class="master-container" :model="formData" :rules="formDataRules" size="mini"
        label-width="100px" :show-message="false">
        <el-row :gutter="30">
          <el-col :span="6">
            <el-form-item label="单号：" required prop="formNo">
              <el-input v-if="editStatus" v-model.trim="formData.formNo" size="mini" type="text" placeholder="采购单号"
                :clearable="true" />
              <span v-else>{{ formData.formNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="发票单号：" required>
              <el-input v-if="editStatus" v-model.trim="formData.ticketNum" size="mini" type="text" placeholder="发票单号"
                :clearable="true" />
              <span v-else>{{ formData.ticketNum }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="日期：">
              <el-date-picker v-if="editStatus" v-model.trim="formData.formTime" size="mini" :clearable="false"
                format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" align="center" type="date" placeholder="日期" />
              <span v-else>{{ formData.formTime ? $moment(formData.formTime).format('YYYY-MM-DD') : '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="供应商：" required>
              <template v-if="editStatus && !formData.id">
                <el-input v-model.trim="formData.supplierName" size="mini" type="text" placeholder="供应商" readonly
                  :clearable="false">
                  <el-button slot="append" icon="el-icon-search" @click="() => customerChoiceView()" />
                </el-input>
              </template>
              <span v-else>{{ formData.supplierName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="金额：" required>
              <template v-if="editStatus && !formData.id">
                <el-input-number v-model="formData.priceTax" :controls="false" :min="0" :precision="2">{{
                  formData.priceTax
                }}</el-input-number>
              </template>
              <span v-else>{{ formData.priceTax }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="冲销金额：" required>
              <template v-if="editStatus">
                <el-input-number v-model="formData.priceTaxReversal" :controls="false" :min="0" :precision="2"
                  disabled>{{
                    formData.priceTax
                  }}</el-input-number>
              </template>
              <span v-else>{{ formData.priceTaxReversal }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="税率：" required>
              <template v-if="editStatus">
                <el-input-number v-model="formData.taxRate" :controls="false" :min="0" :precision="2">{{
                  formData.taxRate
                }}</el-input-number>
              </template>
              <span v-else>{{ formData.taxRate }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注：">
              <el-input v-if="editStatus" v-model.trim="formData.remark" size="mini" type="text" placeholder="备注"
                :clearable="true" />
              <span v-else>{{ formData.remark }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <!-- 从表 -->
      <div class="details-container">
        <el-row>
          <el-col>
            <div class="details-head">
              <div class="title"><i class="fa fa-th-list" aria-hidden="true" /> 明细</div>
              <div v-if="editStatus" class="details-btns-container">
                <el-button v-if="!formData.id" :disabled="!(formData.supplierId)" type="primary" size="mini" @click="handleAddProduct"><i
                    class="fa fa-plus" aria-hidden="true" />
                  添加</el-button>
                <el-button v-if="!formData.id" type="danger" size="mini" :disabled="selections.length === 0" @click="handleDelete"><i
                    class="fa fa-trash" aria-hidden="true" />
                  删除</el-button>
                <el-button v-if="!formData.id" type="danger" size="mini" :disabled="formData.details.length === 0"
                  @click="handleBatchDeleteDetails"><i class="el-icon-delete" aria-hidden="true" /> 全部删除</el-button>
                <el-button v-if="!formData.id" type="primary" size="mini" :disabled="formData.details.length === 0"
                  @click="handleReversal"><i class="el-icon-delete" aria-hidden="true" /> 全部冲销</el-button>
              </div>
            </div>
            <div class="details-body">
              <el-table ref="partsTable" :data="formData.details" size="mini" height="100%" border
                header-row-class-name="list-header-row" highlight-current-row :row-class-name="setRowLine"
                @row-click="detailRowClick" @selection-change="handleSelect">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column type="index" label="行号" width="47" align="center" />
                <el-table-column label="采购单号" width="150" align="center">
                  <template #default="scope">
                    <div>{{ scope.row.formNo }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="材质" width="150" align="center">
                  <template #default="scope">
                    <div>{{ scope.row.baseMaterialName }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="加工件" width="150" align="center">
                  <template #default="scope">
                    <div>{{ scope.row.partName }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="数量" width="150" align="center">
                  <template #default="scope">
                    <div>{{ scope.row.num }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="含税单价" width="150" align="center">
                  <template #default="scope">
                    <div>{{ scope.row.price }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="含税金额" width="150" align="center">
                  <template #default="scope">
                    <div>{{ scope.row.money }}</div>
                  </template>
                </el-table-column>
                <el-table-column v-if="!formData.id" label="可冲销" width="150" align="center">
                  <template #default="scope">
                    <div>{{ scope.row.canReversalMoney.toFixed(2) }}</div>
                  </template>
                </el-table-column>
                <el-table-column :key="typeKey" label="冲销金额" width="150" align="center">
                  <template #default="scope">
                    <div>
                      <el-input-number v-model.trim="scope.row.reversalMoney" :clearable="true" :precision="2"
                        :controls="false" controls-position="right" disabled />
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="备注" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.remark" size="mini" type="text" placeholder="备注"
                        :clearable="true" />
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
      <dialog-customer-choice ref="customerSingleChoice" :multiple="false" :parent="this" :width="'800px'"
        :simple-selected="customerSimpleChoice" />
      <DialogPurchaseOrderChoice ref="DialogPurchaseOrderChoice" :multiple="false" :parent="this" :width="'800px'"
        :simple-selected="purchaseRequisitionChoice" />
      <DialogPurchaseOrderDetailsChoice ref="DialogPurchaseOrderDetailsChoice" :selected="formData.details"
        :purchase-apply-no="formData.purchaseApplyNo" :purchasing-requisition-id="formData.purchasingRequisitionId"
        :base-material-category-id="formData.baseMaterialCategoryId"
        :base-material-category-name="formData.baseMaterialCategoryName" :multiple="true" :parent="this"
        :width="'1200px'" :multiple-selected="purchaseRequisitionChoiceDetails" />
    </div>
  </el-drawer>
</template>

<script>
/**
 * 原材料，有加工件名称
 * 其他，物料名称
 */
import store from '@/store'
import { deepClone } from '@/utils'
import validate from '@/utils/validate'
import ticketReversalApi from '@/api/biz/ticketreversal'
import DialogCustomerChoice from './DialogCustomerChoice.vue'
import DialogPurchaseOrderDetailsChoice from './DialogPurchaseOrderDetailsChoice.vue'
import DialogPurchaseOrderChoice from './DialogPurchaseOrderChoice.vue'
export default {
  name: 'PurchaeOrderForm',
  components: {
    DialogCustomerChoice,
    DialogPurchaseOrderChoice,
    DialogPurchaseOrderDetailsChoice
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
      company: {},
      currentDetails: {},
      companyMaterials: {},
      typeKey: '000',
      selections: [],
      // 主表单数据结构
      formEmptyData: {
        id: null,
        saleOrderDetailId: '',
        mouldCode: '',
        saleFormNo: '',
        formNo: '',
        formDate: this.$moment(new Date()).format('YYYY-MM-DD HH:mm:ss'),
        supplierId: '',
        supplierName: '',
        remark: '',
        details: [],
        creatorName: store.getters.name ? store.getters.name : '',
        // 只有当采购类别是material时为true显示长宽高
        // 其他是尺寸
        detailStyleSize: false
      },
      // 主表校验规则
      formDataRules: {
        formNo: [{ required: true, trigger: 'blur', message: '单号不能为空！' }]
      },
      // 采购类别
      purchaseCategory: [],

      addProductNum: 1,

      heatArray: [
        { value: 1, name: '是' },
        { value: 0, name: '否' }
      ]
    }
  },
  created() {
    this.formData = deepClone(this.formEmptyData)
  },
  mounted() { },
  methods: {
    // 记录行号
    setRowLine({ row, rowIndex }) {
      row.line = rowIndex + 1
    },
    open(id) {
      // console.log('open')
      id = id || '0'
      this.formData = deepClone(this.formEmptyData)
      this.loadData(id)
    },
    async loadData(id) {
      const res = await ticketReversalApi.getById(id)
      console.log(res, 'res')
      if (res.data) {
        this.formData = res.data
        this.getCompanyInfo(res.data.supplierId)
      } else {
        this.formData = JSON.parse(JSON.stringify(this.formEmptyData))
        this.editStatus = true
      }
      this.visible = true
    },
    /**
     * 窗口打开动画完成
     */
    opened() {
      if (this.formData.details.length > 0) {
        this.$refs.partsTable.setCurrentRow(this.formData.details[0])
      }
    },

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
    handleReversal() {
      this.formData.priceTaxReversal = 0
      // this.formData.details.forEach(element => {
      //   if ((this.formData.priceTaxReversal + element.money) <= this.formData.priceTax) {
      //     this.formData.priceTaxReversal = this.formData.priceTaxReversal + element.money
      //     element.reversalMoney = element.money
      //   } else {
      //     element.reversalMoney = this.formData.priceTax - this.formData.priceTaxReversal + element.money
      //     this.formData.priceTaxReversal = this.formData.priceTax
      //     return
      //   }
      // })
      for (let i = 0; i < this.formData.details.length; i++) {
        if ((this.formData.priceTaxReversal + this.formData.details[i].canReversalMoney) <= this.formData.priceTax) {
          this.formData.priceTaxReversal = this.formData.priceTaxReversal + this.formData.details[i].canReversalMoney
          this.formData.details[i].reversalMoney = this.formData.details[i].canReversalMoney
        } else {
          this.formData.details[i].reversalMoney = this.formData.priceTax - this.formData.priceTaxReversal
          this.formData.priceTaxReversal = this.formData.priceTax
          break
        }
      }
      this.typeKey = new Date().valueOf()
    },
    // 列表 select change 事件
    handleSelect(selections) {
      this.selections = selections.map(item => item.id)
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
            this.selections.forEach(id => {
              this.formData.details = this.formData.details.filter(item => item.id !== id);
            })
          })
          .catch(() => {
            this.$message.info('已取消删除')
          })
      } else {
        this.$message.warning('请至少选择一条要删除的数据!')
      }
    },
    /**
     * 保存
     */
    async handleSave() {
      if (!this.formData.formNo) {
        this.$message.warning('单号不能为空')
        return
      }
      if (!this.formData.ticketNum) {
        this.$message.warning('发票号不能为空')
        return
      }
      if (this.formData.priceTaxReversal !== this.formData.priceTax) {
        this.$message.warning('金额未完全冲销')
        return
      }
      if (this.formData.details.length === 0) {
        this.$message.warning('至少添加一条明细')
        return
      }
      const res = await ticketReversalApi.save(this.formData)
      if (res.code === 200) {
        console.log(res, 'res')
        this.$message.success(res.message)
        this.loadData(res.data.id)
        this.editStatus = false
        this.parent.loadData()
      } else this.$message.error(res.message)
    },
    // 采购类别改变
    handlerPurchaseCategoryChange() {
      console.log('handlerPurchaseCategoryChange')
      //   供应商切换，就清理从表
      this.formData.details = []

      // console.log(this.formData.baseMaterialCategoryId)
      var id = this.formData.baseMaterialCategoryId
      var find = this.purchaseCategory.find((item) => item.id === id)
      if (find === null) {
        return
      }
      this.formData.baseMaterialCategoryName = find.name

      if (find.code === 'material' || find.name === '原材料' || find.name === '材料') {
        this.formData.detailStyleSize = true
      } else {
        this.formData.detailStyleSize = false
      }
    },

    /**
     * 批量删除产品
     */
    handleBatchDeleteDetails() {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.formData.details = []
          this.formData.priceTaxReversal = 0
        })
        .catch(() => {
          this.$message.info('已取消删除')
        })
    },
    // 删除产品信息
    handleDeProduct(row, index) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.formData.details.splice(index, 1)
        })
        .catch(() => {
          this.$message.info('已取消删除')
        })
    },
    handleAddApply() {
      this.$refs.DialogPurchaseOrderChoice.open()
    },

    // 绑定采购申请
    purchaseRequisitionChoice(row) {
      console.log('选择带回采购申请单主表', row)
      this.formData.purchasingRequisitionId = row.id
      this.formData.purchaseApplyNo = row.formNo
      this.formData.baseMaterialCategoryId = row.baseMaterialCategoryId
      this.formData.baseMaterialCategoryName = row.baseMaterialCategoryName
      this.$forceUpdate()
      this.$refs.DialogPurchaseOrderChoice.close()
    },

    detailRowClick(row) {
      this.currentDetails = row
    },

    priceNtChange() {
      this.$set(this.currentDetails, 'amountNt', this.currentDetails.priceNt * this.currentDetails.number)
      this.$set(this.currentDetails, 'price', this.currentDetails.priceNt * (1 + this.company.tax))
      this.$set(this.currentDetails, 'amount', this.currentDetails.amountNt * (1 + this.company.tax))
      console.log('未税单价', this.currentDetails.priceNt)
      console.log('未税合计', this.currentDetails.amountNt)
      console.log('含税单价', this.currentDetails.price)
      console.log('含税合计', this.currentDetails.amount)
      console.log(this.formData.details)
      this.$forceUpdate()
    },

    // 添加从表(产品) 信息
    handleAddProduct() {
      this.$refs.DialogPurchaseOrderDetailsChoice.open({ supplierId: this.formData.supplierId })
    },

    // 添加采购单明细
    purchaseRequisitionChoiceDetails(selection) {
      this.$refs.DialogPurchaseOrderDetailsChoice.close()
      // console.log('purchaseRequisitionChoice')
      if (!this.formData.details) {
        this.formData.details = []
      }
      var item = null
      for (var i = 0; i < selection.length; i++) {
        item = selection[i]
        console.log(item, 'item')
        item.num = item.number
        // 含税金额
        item.money = item.amount
        item.reversalMoney = 0
        item.materialId = item.baseMaterialId
        item.purchaseReversalId = item.id
        item.canReversalMoney = item.money - item.hasReversalMoney
        this.formData.details.push(item)
      }
      console.log(this.formData.details)
    },

    async getCompanyInfo(companyId) {
      this.companyMaterials = {}
      this.company = {}
      const res = await ticketReversalApi.getCompanyInfo(companyId)
      if (res.code === 200) {
        this.company = res.data
        if (this.company && this.company.materials.length > 0) {
          for (var i = 0; i < this.company.materials.length; i++) {
            this.companyMaterials[this.company.materials[i].materialId] = this.company.materials[i]
          }
        }
        console.log('供应商', this.company)
        console.log('税率:', this.company.tax)
        console.log('材质对象:', this.companyMaterials)
      }
    },

    /**
     * 客户选择对话框
     */
    customerChoiceView() {
      this.$refs.customerSingleChoice.open()
    },
    customerSimpleChoice(info) {
      //   供应商切换，就清理从表
      this.formData.details = []
      this.formData.supplierId = info.id
      this.formData.supplierName = info.name
      this.formData.taxRate = info.tax
      this.$refs.customerSingleChoice.close()
      this.getCompanyInfo(info.id)
    }
  }
}
</script>
