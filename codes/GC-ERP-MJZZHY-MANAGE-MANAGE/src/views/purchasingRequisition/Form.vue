<template>
  <!--单件订单-->
  <el-drawer title="采购申请表单" :visible.sync="visible" :modal-append-to-body="false" :modal="false" direction="rtl"
    size="100%" @opened="opened">
    <div class="page-container form-container">
      <!-- 功能按钮 -->
      <div class="form-btns-container">
        <span class="title-label"><i class="el-icon-document" /> 采购申请表单</span>
        <el-button v-if="(formData.id !== '0' && formData.id !== null)" v-permission="['purchasingRequisition:edit']"
          :type="editStatus ? 'warning' : 'primary'" size="mini" @click="handleEdit"><i class="fa fa-pencil-square-o"
            aria-hidden="true" /> {{ editStatus ? '取消编辑' : '编辑' }}</el-button>
        <el-button v-if="editStatus" type="default" size="mini" @click="handleSave"><i class="fa fa-floppy-o"
            aria-hidden="true" /> 保存</el-button>
        <el-button v-if="!editStatus && formData.approve == 0" v-permission="['purchasingRequisition:apply']"
          type="default" size="mini" @click="handleApprove(1)"><i class="fa fa-floppy-o" aria-hidden="true" />
          审批申请</el-button>
        <el-button v-if="!editStatus && formData.approve == 1" v-permission="['purchasingRequisition:approve']"
          type="default" size="mini" @click="handleApprove(2)"><i class="fa fa-floppy-o" aria-hidden="true" />
          审批通过</el-button>
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
            <el-form-item label="申请单号：">
              <el-input v-if="editStatus" v-model.trim="formData.formNo" size="mini" type="text" placeholder="订单号"
                :clearable="true" />
              <span v-else>{{ formData.formNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="申请日期：">
              <el-date-picker v-if="editStatus" v-model.trim="formData.formDate" size="mini" :clearable="false"
                format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" align="center" type="datetime"
                placeholder="申请日期" />
              <span v-else>{{ formData.formDate ? $moment(formData.formDate).format('YYYY-MM-DD') : '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="期望纳期：">
              <el-date-picker v-if="editStatus" v-model.trim="formData.deadline" size="mini" :clearable="false"
                format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" align="center" type="datetime"
                placeholder="期望纳期" />
              <span v-else>{{ formData.deadline ? $moment(formData.deadline).format('YYYY-MM-DD') : '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="采购类别：" required>
              <el-select v-if="editStatus" v-model.trim="formData.baseMaterialCategoryId" placeholder="采购类别" size="mini"
                :clearable="true" @change="handlerPurchaseCategoryChange">
                <el-option v-for="item in purchaseCategory" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
              <span v-else>{{ formData.baseMaterialCategoryName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="采购类型：" required>
              <el-select v-if="editStatus" v-model.trim="formData.purchasingType" placeholder="采购类型" size="mini"
                :clearable="true" @change="purchaseTypeChange">
                <el-option v-for="item in purchaseType" :key="item.value" :label="item.name" :value="item.value" />
              </el-select>
              <span v-else>{{ formData.purchasingType }}</span>
            </el-form-item>
          </el-col>
          <!--随着采购类型改变的模具编码等-->
          <CodeByPurchaseType :purchase-type="formData.purchasingType" :code="formData.code" :edit-status="editStatus"
            @callback="handleCodeByPurchaseCallBack" />
          <el-col :span="6">
            <el-form-item label="申请部门：">
              <el-input v-if="editStatus" v-model.trim="formData.departmentName" disabled size="mini" type="text"
                placeholder="申请部门" />
              <span v-else>{{ formData.departmentName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="申请人：">
              <el-input v-if="editStatus" v-model.trim="formData.employeeName" size="mini" type="text" placeholder="申请人"
                readonly>
                <el-button slot="append" icon="el-icon-search" @click="handleEmployeeChoiceDialogOpen()" />
              </el-input>
              <span v-else>{{ formData.employeeName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请原因：">
                <el-select v-if="editStatus" v-model="formData.reason" size="mini" placeholder="请选择申请原因">
                  <el-option v-for="item in reasons" :key="item.id" :label="item.dictValue" :value="item.dictValue" />
                </el-select>
                <span v-else>{{ formData.reason }}</span>
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
              <div class="title"><i class="fa fa-th-list" aria-hidden="true" /> 采购明细</div>
              <div v-if="editStatus" class="details-btns-container">
                <el-button type="primary" size="mini" @click="handleAddProduct"><i class="fa fa-plus"
                    aria-hidden="true" />
                  添加</el-button>
                <el-input-number v-model="addProductNum" size="mini" :min="1" controls-position="right"
                  style="width: 60px;" />
                <div
                  style="display: inline-block; padding-top: 5px; padding-right: 10px; padding-left: 2px; font-size: 12px;">
                  条</div>
                <el-button type="danger" size="mini" :disabled="formData.details.length === 0"
                  @click="handleBatchDeleteProducts"><i class="el-icon-delete" aria-hidden="true" /> 全部删除</el-button>
              </div>
            </div>
            <div class="details-body">
              <el-table ref="partsTable" :data="formData.details" size="mini" height="100%" border
                header-row-class-name="list-header-row" highlight-current-row @current-change="handleProductChange">
                <el-table-column type="index" label="行号" width="47" align="center" />
                <el-table-column label="排序" width="60" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.sort" size="mini" type="text" placeholder="规格" disabled
                        :clearable="true" />
                    </div>
                    <div v-else>{{ scope.row.sort }}</div>
                  </template>
                </el-table-column>
                <el-table-column v-if="formData.detailStyleSize" key="4" label="加工件名称" width="200px" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.partName" size="mini" type="text" placeholder="加工件名称"
                        disabled />
                    </div>
                    <div v-else>{{ scope.row.partName }}</div>
                  </template>
                </el-table-column>

                <el-table-column :label="!formData.detailStyleSize ? '零件名称' : '材质'" width="150" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.baseMaterialName" size="mini" placeholder="材质" readonly
                        disabled>
                        <el-button slot="append" icon="el-icon-search" disabled
                          @click="handleOpenMaterialDialog(scope.row)" />
                      </el-input>
                    </div>
                    <div v-else>{{ scope.row.baseMaterialName }}</div>
                  </template>
                </el-table-column>
                <el-table-column v-if="!formData.detailStyleSize" key="0" label="规格" width="200px" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.standard" size="mini" type="text" placeholder="规格"
                        :clearable="true" disabled />
                    </div>
                    <div v-else>{{ scope.row.standard }}</div>
                  </template>
                </el-table-column>
                <el-table-column v-if="formData.detailStyleSize" key="1" label="长" width="65px" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input-number v-model.trim="scope.row.length" size="mini" placeholder="长" :clearable="true"
                        :precision="0" controls-position="right" disabled />
                    </div>
                    <div v-else>{{ scope.row.length }}</div>
                  </template>
                </el-table-column>
                <el-table-column v-if="formData.detailStyleSize" key="2" label="宽" width="65px" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input-number v-model.trim="scope.row.width" size="mini" placeholder="宽" :clearable="true"
                        :precision="0" controls-position="right" disabled />
                    </div>
                    <div v-else>{{ scope.row.width }}</div>
                  </template>
                </el-table-column>
                <el-table-column v-if="formData.detailStyleSize" key="3" label="高" width="65px" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input-number v-model.trim="scope.row.height" size="mini" placeholder="宽" :clearable="true"
                        :precision="0" controls-position="right" disabled />
                    </div>
                    <div v-else>{{ scope.row.height }}</div>
                  </template>
                </el-table-column>

                <el-table-column v-if="formData.detailStyleSize" label="热处理" width="80" align="center">
                  <template #default="scope">
                    <el-select v-if="editStatus" v-model.trim="scope.row.heatTreatment" size="mini" :clearable="true"
                      disabled>
                      <el-option v-for="item in heatArray" :key="item.value" :label="item.name" :value="item.value" />
                    </el-select>
                    <div v-else>{{ scope.row.heatTreatmentName }}</div>
                  </template>
                </el-table-column>

                <el-table-column label="数量" width="60" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input-number v-model.trim="scope.row.number" size="mini" placeholder="数量" :clearable="true"
                        :precision="0" controls-position="right" disabled />
                    </div>
                    <div v-else>{{ scope.row.number }}</div>
                  </template>
                </el-table-column>
                <el-table-column v-if="!formData.detailStyleSize" label="单位" width="60" align="center">
                  <template #default="scope">
                    <div v-if="editStatus">
                      <el-input v-model.trim="scope.row.numberUnit" size="mini" placeholder="单位" :clearable="true"
                        disabled />
                    </div>
                    <div v-else>{{ scope.row.numberUnit }}</div>
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
      <!-- 选择员工对话框，带部门 -->
      <dialog-department-employee-choice ref="employeeSimpleChoice" :simple-selected="handleEmployeeSimpleSelected" />
      <dialog-material-choice ref="materialSingleChoice" :multiple="false" :parent="this" :width="'1000px'"
        :simple-selected="materialSimpleChoice" />
    </div>
  </el-drawer>
</template>

<script>
import store from '@/store'
import { deepClone } from '@/utils'
import { getToken } from '@/utils/auth'
import validate from '@/utils/validate'
import dataDictionary from '@/api/sys/dataDictionary.js'
import purchase_apply_api from '@/api/biz/purchasingRequisition'
import DialogMaterialChoice from '@/views/dialogs/DialogMaterialChoice.vue'
import DialogDepartmentEmployeeChoice from '@/views/dialogs/DialogDepartmentEmployeeChoiceV2.vue'
import CodeByPurchaseType from './CodeByPurchaseType.vue'
export default {
  name: 'PurchaeApplyForm',
  components: {
    DialogMaterialChoice,
    DialogDepartmentEmployeeChoice,
    CodeByPurchaseType
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
      reasons: [],
      formData: {},
      // 主表单数据结构
      formEmptyData: {
        id: null,
        saleOrderDetailId: '',
        mouldCode: '',
        saleFormNo: '',
        formNo: '',
        formDate: this.$moment(new Date()).format('YYYY-MM-DD HH:mm:ss'),
        customerId: '0',
        customerName: '',
        employeeId: '',
        employeeName: '',
        departmentId: '',
        departmentName: '',
        remark: '',
        details: [],
        creatorName: store.getters.name ? store.getters.name : '',

        purchasingType: '',
        code: '',
        // 只有当采购类别是material时 显示长宽高
        // 其他是尺寸
        detailStyleSize: false
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
      dataDictionary: [],
      accepters: [],
      accuracies: [],
      addProductNum: 1,
      setHeaders: {
        Authorization: getToken()
      },
      currentRow: null,

      // 采购类别
      purchaseCategory: [],
      // 采购类型
      purchaseType: [
        {
          value: '模具',
          name: '模具'
        },
        {
          value: '自动化',
          name: '自动化'
        },
        {
          value: '零件',
          name: '零件'
        },
        {
          value: '冲压',
          name: '冲压'
        }
      ],

      heatArray: [
        {
          value: 1,
          name: '是'
        },
        {
          value: 0,
          name: '否'
        }
      ]
    }
  },
  created() {
    this.formData = deepClone(this.formEmptyData)
  },
  mounted() {
    this.init()
  },
  methods: {
    open(id) {
      // console.log('open')
      id = id || '0'
      this.formData = deepClone(this.formEmptyData)
      this.currentRow = null
      this.loadData(id)
    },
    async init() {
      var param = {
        categoryCode: 'purchasing_requisition'
      }
      const res = await dataDictionary.getList(param)
      if (res.code === 200) {
        this.reasons = res.data.list
      }
    },
    /**
     * 2022-5-19 由主页面打开模拟采购申请
     */
    async simulationPurchaseApply(param) {
      // console.log('simulationPurchaseApply')
      const res = await purchase_apply_api.simulationPurchaseApply(param)
      if (res.code === 200) {
        this.purchaseCategory = res.data.purchaseCategory
        if (res.data.formData) {
          this.formData = res.data.formData
          this.formData.creatorName = store.getters.name ? store.getters.name : ''
          this.formData.createTime = this.$moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
          this.editStatus = true
        } else {
          this.editStatus = false
        }
        this.visible = true
      } else this.$message.error(res.message)
    },
    loadData(id) {
      console.log('loadData')
      purchase_apply_api.getById(id).then((res) => {
        this.purchaseCategory = res.data.purchaseCategory
        if (res.data.formData) {
          this.formData = res.data.formData
        } else {
          this.formData = JSON.parse(JSON.stringify(this.formEmptyData))
          this.editStatus = true
          // 默认
          if (this.purchaseCategory && this.purchaseCategory.length >= 1) {
            this.formData.baseMaterialCategoryId = this.purchaseCategory[0].id
            this.handlerPurchaseCategoryChange()
          }
        }
        this.visible = true
      })
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

    /**
     * 保存
     */
    handleSave() {
      console.log('asd')
      if (this.formData.baseMaterialCategoryId === null || this.formData.baseMaterialCategoryId === undefined || this.formData.baseMaterialCategoryId === 0 || this.formData.purchasingType === '') {
        this.$message.warning('没有指定采购类别')
        return
      }

      if (this.formData.purchasingType === null || this.formData.purchasingType === undefined || this.formData.purchasingType === 0 || this.formData.purchasingType === '') {
        this.$message.warning('没有指定采购类型')
        return
      }

      if (this.formData.details.length === 0) {
        this.$message.warning('至少添加一条产品信息')
        return
      }

      purchase_apply_api.save(this.formData).then((res) => {
        if (res.code === 200) {
          this.$message.success(res.message)
          this.loadData(res.data.id)
          this.editStatus = false
          this.parent.loadData()
        } else this.$message.error(res.message)
      })
    },

    handleEmployeeChoiceDialogOpen() {
      this.$refs.employeeSimpleChoice.open()
    },
    // 默认执行人
    async handleEmployeeSimpleSelected(info) {
      // console.log('handleEmployeeSimpleSelected')
      this.formData.departmentId = info.departmentId
      this.formData.departmentName = info.departmentName
      this.formData.employeeId = info.employeeId
      this.formData.employeeName = info.employeeName
      this.$refs.employeeSimpleChoice.close()
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
        .then(async () => {
          this.formData.details = []
          this.currentRow = null
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
        .then(async () => {
          this.formData.products.splice(index, 1)
          if (this.formData.products.length > 0) {
            this.handleProductChange(this.formData.products[0])
          } else {
            this.currentRow = null
          }
        })
        .catch(() => {
          this.$message.info('已取消删除')
        })
    },
    // 添加从表(产品) 信息
    handleAddProduct() {
      const addProductNum = this.addProductNum
      for (var i = 0; i < addProductNum; ++i) {
        // 此处必须进行深拷贝，否则添加的明细，将都指向了一个对象
        const newProduct = deepClone(this.productEmptyData)
        this.formData.details.push(newProduct)
      }
      if (this.formData.details && this.formData.details.length > 0) {
        this.$refs.partsTable.setCurrentRow(this.formData.details[0])
      }
    },
    handleProductChange(row) {
      if (row) {
        this.currentRow = row
      } else {
        if (this.formData.details && this.formData.details.length > 0) {
          this.$refs.partsTable.setCurrentRow(this.formData.details[0])
        } else {
          this.currentRow = null
        }
      }
    },

    /**
     * 选择材质对话框
     */
    handleOpenMaterialDialog(row) {
      this.currentRow = row
      this.$refs.materialSingleChoice.open()
    },
    materialSimpleChoice(info) {
      // console.log(info)
      this.currentRow.baseMaterialCategoryId = info.categoryId
      this.currentRow.baseMaterialId = info.id
      this.currentRow.baseMaterialCode = info.code
      this.currentRow.baseMaterialName = info.name

      this.$refs.materialSingleChoice.close()
    },

    // 采购类型改变
    purchaseTypeChange() {
      // console.log(this.formData.purchasingType)
      // 切换采购类型,清空明细列表
      this.formData.details = []
      this.currentRow = null
    },
    // 采购类别改变
    handlerPurchaseCategoryChange() {
      // console.log(this.formData.baseMaterialCategoryId)

      // 切换采购类别时,也清空明细的列表
      // 切换采购类型,清空明细列表
      this.formData.details = []
      this.currentRow = null

      var id = this.formData.baseMaterialCategoryId
      var find = this.purchaseCategory.find((item) => item.id === id)
      if (find === null) {
        return
      }
      if (find.code === 'material') {
        this.formData.detailStyleSize = true
      } else {
        this.formData.detailStyleSize = false
      }
    },
    handleCodeByPurchaseCallBack(data) {
      // 别管啥号,也清空明细列表
      this.formData.details = []
      this.currentRow = null

      if (data.type === 'sale') {
        // 模具
        this.formData.saleOrderId = data.saleOrderId
        this.formData.saleOrderDetailId = data.saleOrderDetailId
        this.formData.designAssignmentId = data.designAssignmentId
      } else if (data.type === 'auto') {
        // 自动化
        this.formData.autoOrderId = data.autoOrderId
        this.formData.autoOrderDetailId = data.autoOrderDetailId
        this.formData.autoDesignAssignmentId = data.autoDesignAssignmentId
      }
    },
    // 2023-05-29 追加审批通过的按钮
    handleApprove(val) {
      this.formData.approve = val
      var param = {
        id: this.formData.id,
        approve: this.formData.approve
      }
      purchase_apply_api.approve(param).then((res) => {
        if (res.code === 200) {
          this.$message.success(res.message)
          this.loadData(this.formData.id)
          this.editStatus = false
          this.parent.loadData()
        } else this.$message.error(res.message)
      })
    }
  }
}
</script>
