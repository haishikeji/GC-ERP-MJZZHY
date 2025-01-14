<template>
  <el-drawer title="制定工艺表单" :visible.sync="visible" :modal-append-to-body="false" custom-class="hidden-title" direction="rtl" size="100%">
    <div class="page-container form-container" style="height: 100%;">
      <!-- 功能按钮 -->
      <div class="form-btns-container">
        <div style="padding-top: 4px;">
          <span class="title-label"><i class="el-icon-document" /> 制定工艺表单</span>
        </div>
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
      <div style="flex: 2; padding: 0 2px;">
        <el-row :gutter="2" style="height: 100%;">
          <el-col :span="7" style="height: 100%;">
            <!--数据字典类别列表 开始-->
            <div class="page-container list-container">
              <!--操作按钮 开始-->
              <div class="list-btns-container">
                <span class="title-label"><i class="el-icon-document" /> 待选工序</span>
                <el-button type="success" size="mini" :disabled="selection.length === 0" @click="handleConfirmProcess"><i class="el-icon-finished" aria-hidden="true" /> 添加至【工艺单】</el-button>
              </div>
              <!--数据字典类别列表数据 开始-->
              <div class="el-table-container">
                <div>
                  <el-table ref="processes" :data="processes" size="mini" border header-row-class-name="list-header-row" row-class-name="list-row" height="100%" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="40" align="center" />
                    <el-table-column type="index" label="行号" width="40" align="center" />
                    <el-table-column label="工序名称" prop="code" header-align="center" />
                    <el-table-column label="工序编码" prop="name" header-align="center" />
                  </el-table>
                </div>
              </div>
              <!--数据字典类别列表数据 结束-->
            </div>
          </el-col>
          <el-col :span="17" style="height: 100%;">
            <!--数据字典类别列表 开始-->
            <div class="page-container list-container">
              <!--操作按钮 开始-->
              <div class="list-btns-container">
                <span class="title-label"><i class="el-icon-document" /> 工艺单</span>
                <!-- <el-button v-permission="['bomPartProcess:standardImport']" type="primary" size="mini" @click="handleOpenStandardProcessDialog"><i class="el-icon-download" aria-hidden="true" /> 从标准工艺库导入</el-button> -->
                <el-button type="default" size="mini" :disabled="bomPartProcesses.length === 0" @click="handleSave"><i class="fa fa-floppy-o" aria-hidden="true" /> 保存</el-button>
                <el-button v-permission="['bomPartProcess:delete']" type="danger" size="mini" :disabled="bomPartProcessSelection.length === 0" @click="handleBatchDeleteBomPartProcesses">
                  <i class="el-icon-delete" aria-hidden="true" /> 删除选择项
                </el-button>
              </div>
              <!--数据字典类别列表数据 开始-->
              <div class="el-table-container bom-part-process-form-sort-table-container">
                <div>
                  <el-table :data="bomPartProcesses" size="mini" border row-key="uuid" header-row-class-name="list-header-row" :row-class-name="tableRowClassName" height="100%" @selection-change="handleBomPartProcessSelectionChange">
                    <el-table-column label="移动" width="40" align="center">
                      <div class="move-handle">
                        <i class="el-icon-rank" aria-hidden="true" />
                      </div>
                    </el-table-column>
                    <el-table-column type="selection" width="40" align="center" />
                    <el-table-column type="index" label="行号" width="47" align="center" />
                    <el-table-column label="工序名称" width="80" align="center" show-overflow-tooltip>
                      <template #default="scope">
                        <ProcessChangeInside :key="scope.row.sort" :processes="processes" :row="scope.row" />
                      </template>
                    </el-table-column>
                    <el-table-column label="计划工时(小时)" width="90" align="center">
                      <template #default="scope">
                        <div>
                          <el-input-number v-model.trim="scope.row.workHour" size="mini" :min="0" placeholder="计划工时" :precision="2" controls-position="right" @focus="$selectAll" />
                        </div>
                      </template>
                    </el-table-column>
                    <!--
                    <el-table-column label="计划完工日期" align="center" width="140">
                      <template #default="scope">
                        <div v-if="editStatus">
                          <el-date-picker v-model.trim="scope.row.deadline" size="mini" :clearable="true" format="yyyy-MM-dd" value-format="yyyy-MM-dd HH:mm:ss" align="center" type="date" placeholder="请选择" />
                        </div>
                        <div v-else>{{ scope.row.deadline === null ? '-' : $moment(scope.row.deadline).format('YYYY-MM-DD') }}</div>
                      </template>
                    </el-table-column>
                    -->
                    <el-table-column label="加工内容" align="center">
                      <!-- <template #default="scope">
                        <el-input v-model.trim="scope.row.remark" size="mini">
                          <el-button slot="append" icon="el-icon-search" @click="handleOpenProcessContent(scope.row)" />
                        </el-input>
                      </template> -->
                      <template #default="scope">
                        <!-- <ProcessContentSelect :part-name="parent.currentMachiningPart.partName" :row="scope.row" /> -->
                        <ProcessContentInput :key="scope.row.sort" :part-name="parent.currentMachiningPart.partName" :row="scope.row" />
                      </template>
                    </el-table-column>

                    <el-table-column label="排序" prop="sort" width="50" align="center" />
                    <!-- 2022-6-20,不显示排序的按钮 -->
                    <!-- <el-table-column label="操作" width="64" align="center">
                      <template #default="scope">
                        <div>
                          <el-button type="success" size="mini" icon="el-icon-top" :disabled="scope.$index===0" circle @click.stop.prevent="handleMove(-1, scope.$index, scope.row)" />
                          <el-button type="warning" size="mini" icon="el-icon-bottom" :disabled="scope.$index===bomPartProcesses.length - 1" circle @click.stop.prevent="handleMove(1, scope.$index, scope.row)" />
                        </div>
                      </template>
                    </el-table-column> -->
                  </el-table>
                </div>
              </div>
              <!--数据字典类别列表数据 结束-->
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <dialog-standard-process-choice ref="dialogStandardProcessChoice" :parent="this" :simple-selected="standardProcessSimpleChoice" />
    <DialogProcessContent ref="DialogProcessContent" />
  </el-drawer>
</template>
<script>
// import dataDictionaryApi from '@/api/sys/dataDictionary'
import dataDictionaryApi from '@/api/biz/process'
import bomPartProcessApi from '@/api/biz/bomPartProcess'
import DialogStandardProcessChoice from '@/views/dialogs/DialogStandardProcessChoice.vue'
import DialogProcessContent from '@/views/dialogs/DialogProcessContent.vue'
import { getToken } from '@/utils/auth'

// 用于工序的切换
import ProcessChangeInside from '@/views/base/standardProcess/ProcessChangeInSide'
// 2022-6-22 用于工序内容
import ProcessContentInput from '@/views/base/standardProcess/ProcessContentInput.vue'
export default {
  name: 'BomPartProcessForm',
  components: {
    DialogStandardProcessChoice,
    DialogProcessContent,
    ProcessContentInput,
    ProcessChangeInside
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
      processes: [],
      query: {
        name: '',
        // 物料类别id
        categoryId: 0,
        total: 0,
        pageSize: this.$defaulPageSize,
        pn: 1
      },
      selection: [],
      oldIndex: 0,
      newIndex: 0,
      bomPartProcessSelection: [],
      // 工序空数据
      processEmptyData: {
        uuid: '',
        id: null,
        saleOrderId: '',
        saleOrderProductId: '',
        saleOrderDetailId: '',
        bomPartId: '',
        processId: '',
        processName: '',
        workHour: '',
        deadline: '',
        remark: '',
        sort: '',
        editStatus: false
      },
      bomPartProcesses: [],
      setHeaders: {
        Authorization: getToken()
      },
      /**
       * 用于产生唯一标识，
       * 以实现“工序”拖动排序
       */
      maxUuid: 0
    }
  },
  mounted: function () {},
  methods: {
    /**
     * 对话框打开 事件
     */
    open() {
      this.visible = true
      this.loadData()
    },
    /**
     * 加载数据
     */
    async loadData() {
      // console.log('loadData')
      const res = await dataDictionaryApi.getList({ size: null })
      this.processes = res.data.list
      if (this.processes.length > 0) {
        this.processes = res.data.list
      }
      this.bomPartProcesses = this.parent.currentMachiningPart.bomPartProcesses
      var _this = this
      _this.maxUuid = 0
      if (this.bomPartProcesses.length > 0) {
        this.bomPartProcesses.forEach((item) => {
          ++_this.maxUuid
          item.uuid = _this.maxUuid
        })
        this.rowDrop()
      }
    },
    /**
     * 对话框关闭 事件
     */
    handleClose() {
      this.visible = false
      this.bomPartProcesses = []
      this.maxUuid = 0
      this.parent.loadBomPartProcess()
    },
    /**
     * 全屏缩放
     */
    handleScreen() {
      const dom = document.querySelector('.page-container > .el-drawer__wrapper')
      this.isFullscreen = !this.isFullscreen
      dom.style.position = this.isFullscreen ? 'fixed' : 'absolute'
    },
    /* ************************************** 待选的工序 相关事件 ************************************** */
    /**
     * 列表checkbox列选择 事件
     */
    handleSelectionChange(selection) {
      this.selection = selection
    },
    /**
     * 确定选择
     */
    handleConfirmProcess() {
      // console.log('handleConfirmProcess')
      const _this = this
      _this.selection.forEach((item) => {
        ++_this.maxUuid
        const newProcess = JSON.parse(JSON.stringify(_this.processEmptyData))
        newProcess.uuid = _this.maxUuid
        newProcess.saleOrderId = _this.parent.currentMould.saleOrderId
        newProcess.saleOrderProductId = _this.parent.currentMould.saleOrderProductId
        newProcess.saleOrderDetailId = _this.parent.currentMould.id
        newProcess.bomPartId = _this.parent.currentMachiningPart.id
        newProcess.processId = item.id
        newProcess.processName = item.name
        _this.bomPartProcesses.push(newProcess)
      })
      _this.setProcessSort()
      _this.rowDrop()

      // 2022-6-20 应客户要求,添加后,不在左侧维持选中状态。
      this.$refs.processes.clearSelection()
    },

    /* ************************************** 工艺卡 相关事件 ************************************** */
    handleOpenStandardProcessDialog() {
      // console.log('handleOpenStandardProcessDialog')
      // 2022-4-12,打开的时候，根据加工价名称，做为一级的工艺名称
      if (this.parent && this.parent.currentMachiningPart && this.parent.currentMachiningPart.partName) {
        var partName = this.parent.currentMachiningPart.partName
        this.$refs.dialogStandardProcessChoice.openByFirstName(partName)
      }
    },
    standardProcessSimpleChoice(info) {
      // console.log('standardProcessSimpleChoice')
      const _this = this
      info.details.forEach((item) => {
        ++_this.maxUuid
        const newProcess = JSON.parse(JSON.stringify(_this.processEmptyData))
        newProcess.uuid = _this.maxUuid
        newProcess.saleOrderId = _this.parent.currentMould.saleOrderId
        newProcess.saleOrderProductId = _this.parent.currentMould.saleOrderProductId
        newProcess.saleOrderDetailId = _this.parent.currentMould.id
        newProcess.bomPartId = _this.parent.currentMachiningPart.id
        newProcess.processId = item.processId
        newProcess.processName = item.processName
        newProcess.workHour = item.workHour
        newProcess.remark = item.remark
        _this.bomPartProcesses.push(newProcess)
      })
      _this.$refs.dialogStandardProcessChoice.close()
      _this.setProcessSort()
      _this.rowDrop()
    },
    handleImportFromExcel() {},
    tableRowClassName({ row }) {
      if (row.dayWorkCnt > 0) {
        return 'disableDrag'
      } else {
        return 'list-row'
      }
    },
    // 行拖拽
    rowDrop() {
      // 此时找到的元素是要拖拽元素的父容器
      const tbody = document.querySelector('.bom-part-process-form-sort-table-container .el-table__body-wrapper tbody')
      const _this = this
      console.log(tbody)
      this.$Sortable.create(tbody, {
        // 指定父元素下可被拖拽的子元素
        draggable: '.list-row',
        handle: '.move-handle',
        filter: '.disableDrag',
        setData(/** DataTransfer */dataTransfer, /** HTMLElement*/dragEl) {
          dataTransfer.setData('Text', dragEl.textContent) // ' dataTransfer '对象的HTML5 DragEvent
        },
        onFilter(evt) {
          console.log(evt)
        },
        // onChoose(evt) {
        //   _this.oldIndex = evt.oldIndex
        //   if (_this.bomPartProcesses[_this.oldIndex].dayWorkCnt > 0) {
        //     _this.$message.error('此工序已报工不能拖拽')
        //   }
        // },
        // onMove(evt, originalEvent) {
        //   console.log(evt)
        //   if (_this.bomPartProcesses[_this.oldIndex].dayWorkCnt > 0) {
        //     return false
        //   }
        // },
        onEnd({ newIndex, oldIndex }) {
          if (newIndex === '' || oldIndex === '') {
            return
          }
          const currRow = _this.bomPartProcesses.splice(oldIndex, 1)[0]
          _this.bomPartProcesses.splice(newIndex, 0, currRow)
          _this.setProcessSort()
        }
      })
    },
    /*
    // 列拖拽
    columnDrop() {
      const wrapperTr = document.querySelector('.el-table__header-wrapper tr')
      this.$Sortable.create(wrapperTr, {
        animation: 180,
        delay: 0,
        onEnd: evt => {
          const oldItem = this.dropCol[evt.oldIndex]
          this.dropCol.splice(evt.oldIndex, 1)
          this.dropCol.splice(evt.newIndex, 0, oldItem)
        }
      })
    },
    */
    setProcessSort() {
      this.bomPartProcesses.forEach((item, index) => {
        item.sort = index + 1
      })
    },
    handleMove(number, index, row) {
      const newIndex = index + number
      this.bomPartProcesses.splice(index, 1)
      this.bomPartProcesses.splice(newIndex, 0, row)
      this.setProcessSort()
    },
    handleBomPartProcessSelectionChange(selection) {
      this.bomPartProcessSelection = selection
    },
    handleBatchDeleteBomPartProcesses() {
      const _this = this
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async () => {
          _this.bomPartProcessSelection.forEach((item) => {
            const index = _this.bomPartProcesses.indexOf(item)
            _this.bomPartProcesses.splice(index, 1)
          })
        })
        .catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    /**
     * 保存
     */
    async handleSave() {
      const res = await bomPartProcessApi.save(this.bomPartProcesses)
      if (res.code === 200) {
        this.$message.success(res.message)
        this.handleClose()
      } else this.$message.error(res.message)
    },
    handleOpenProcessContent: function (row) {
      this.$refs.DialogProcessContent.open(row)
    }
  }
}
</script>
<style scoped>
.bom-part-process-form-sort-table-container >>> .el-table__body-wrapper tbody .el-table__row .move-handle {
  cursor: move;
  background-color: #eee;
}
</style>
