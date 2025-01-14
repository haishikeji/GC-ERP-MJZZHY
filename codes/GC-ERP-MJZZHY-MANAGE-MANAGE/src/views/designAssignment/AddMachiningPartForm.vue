<template>
  <el-drawer title="新增Bom（加工件）表单" class="add-machining-part-form" :visible.sync="visible" :modal-append-to-body="false" :modal="false" custom-class="hidden-title" direction="rtl" size="100%">
    <div class="page-container form-container" style="height: 100%;">
      <!-- 功能按钮 -->
      <div class="form-btns-container">
        <span class="title-label"><i class="el-icon-document" /> 新增Bom（加工件）表单</span>
        <div style="display: inline-block;">
          <el-button type="primary" size="mini" @click="handleAdd"><i class="fa fa-plus" aria-hidden="true" /> 添加</el-button>
          <el-input-number v-model="addMachiningPartNum" size="mini" :min="1" controls-position="right" style="width: 60px;" />
          <div style="display: inline-block; padding-top: 5px; padding-right: 10px; padding-left: 2px; font-size: 12px;">条</div>
        </div>
        <el-button type="default" size="mini" @click="handleSave"><i class="fa fa-floppy-o" aria-hidden="true" /> 保存</el-button>
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
      <div class="details-container" style="display: flex; flex-direction: column; flex: 1;">
        <div class="details-body">
          <el-table :data="machiningParts" size="mini" height="100%" border header-row-class-name="list-header-row" row-class-name="list-row" highlight-current-row>
            <el-table-column label="操作" width="47" align="center">
              <template #default="scope">
                <div v-if="machiningParts.length > 1">
                  <el-button icon="el-icon-close" size="mini" circle type="danger" @click="handleDel(scope.row,scope.$index)" />
                </div>
                <div v-else>-</div>
              </template>
            </el-table-column>
            <el-table-column label="序号" prop="sort" width="60" align="center">
              <template #default="scope">
                <div>
                  <!-- <el-input-number v-model.trim="scope.row.sort" size="mini" placeholder="序号" :clearable="true" :precision="0" controls-position="right" /> -->
                  <el-input v-model.trim="scope.row.sort" size="mini" placeholder="序号" :clearable="true" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="加工件名称" width="100" align="center">
              <template #default="scope">
                <div>
                  <el-input v-model.trim="scope.row.partName" size="mini" placeholder="加工件名称" :clearable="true" />
                </div>
                <span class="required">*</span>
              </template>
            </el-table-column>
            <el-table-column label="材质" width="150" align="center">
              <template #default="scope">
                <div>
                  <el-input v-model.trim="scope.row.baseMaterialName" size="mini" placeholder="材质">
                    <el-button slot="append" icon="el-icon-search" @click="handleOpenMaterialDialog(scope.row)" />
                  </el-input>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="长(mm)" width="90" align="center">
              <template #default="scope">
                <div>
                  <el-input-number v-model.trim="scope.row.length" size="mini" :min="1" placeholder="长" :precision="2" controls-position="right" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="宽(mm)" width="90" align="center">
              <template #default="scope">
                <div>
                  <el-input-number v-model.trim="scope.row.width" size="mini" :min="1" placeholder="宽" :precision="2" controls-position="right" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="厚(mm)" width="90" align="center">
              <template #default="scope">
                <div>
                  <el-input-number v-model.trim="scope.row.thickness" size="mini" placeholder="厚" :precision="2" controls-position="right" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="热处理" width="80" align="center">
              <template #default="scope">
                <div>
                  <el-select v-model.trim="scope.row.heatTreatment" placeholder="热处理" size="mini">
                    <el-option v-for="item in [{lable: '否', value: '否'}, {lable: '是', value: '是'}]" :key="item.label" :label="item.label" :value="item.value" />
                  </el-select>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="60" align="center">
              <template #default="scope">
                <div>
                  <el-input-number v-model.trim="scope.row.number" size="mini" :min="1" placeholder="数量" :precision="0" controls-position="right" />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="备注" align="center">
              <template #default="scope">
                <div>
                  <el-input v-model.trim="scope.row.remark" size="mini" placeholder="备注" :clearable="true" />
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
    <dialog-material-choice ref="materialSingleChoice" :multiple="false" :parent="this" :width="'1000px'" :simple-selected="materialSimpleChoice" />
  </el-drawer>
</template>
<script>
import bomPartApi from '@/api/biz/bomPart'
import DialogMaterialChoice from '@/views/dialogs/DialogMaterialChoice.vue'
export default {
  name: 'AddStandardPartForm',
  components: {
    DialogMaterialChoice
  },
  props: {
    parent: {
      type: Object,
      default: () => null
    },
    mould: {
      type: Object,
      default: () => null
    }
  },
  data: function() {
    return {
      isFullscreen: false,
      visible: false,
      currentMachiningPart: null,
      machiningParts: [],
      // 加工件空数据
      machiningPartEmptyData: {
        id: null,
        saleOrderId: '',
        saleOrderProductId: '',
        saleOrderDetailId: '',
        type: '加工件',
        baseMaterialId: '',
        baseMaterialName: '',
        partName: '',
        length: 1.0,
        width: 1.0,
        thickness: 1.0,
        density: 7.85,
        weight: 0,
        heatTreatment: '否',
        number: 1,
        remark: ''
      },
      addMachiningPartNum: 1
    }
  },
  mounted: function() {},
  methods: {
    /**
     * 对话框打开 事件
     */
    open() {
      this.visible = true
      this.handleAdd()
    },
    /**
     * 对话框关闭 事件
     */
    handleClose() {
      this.visible = false
      this.machiningParts = []
      this.addMachiningPartNum = 1
    },
    /**
     * 全屏缩放
     */
    handleScreen() {
      const dom = document.querySelector('.page-container > .el-drawer__wrapper.add-machining-part-form')
      this.isFullscreen = !this.isFullscreen
      dom.style.position = this.isFullscreen ? 'fixed' : 'absolute'
    },
    /**
     * 每页数据量变化
     */
    handleAdd() {
      for (let i = 0; i < this.addMachiningPartNum; ++i) {
        const newPart = JSON.parse(JSON.stringify(this.machiningPartEmptyData))
        newPart.saleOrderId = this.mould.saleOrderId
        newPart.saleOrderProductId = this.mould.saleOrderProductId
        newPart.saleOrderDetailId = this.mould.id
        this.machiningParts.push(newPart)
      }
    },
    handleDel(row, index) {
      const _this = this
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async() => {
          _this.machiningParts.splice(index, 1)
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
      let lineNo = 0
      for (let i = 0; i < this.machiningParts.length; ++i) {
        if (this.machiningParts[i].partName === '') {
          lineNo = i + 1
          break
        }
      }
      if (lineNo > 0) {
        this.$message.error('第【' + lineNo + '】行数据中的“加工件名称”不能为空')
        return
      }
      const res = await bomPartApi.save(this.machiningParts)
      if (res.code === 200) {
        this.$message.success(res.message)
        this.parent.loadMachiningPartData()
        this.handleClose()
      } else this.$message.error(res.message)
    },
    /**
     * 选择材质对话框
     */
    handleOpenMaterialDialog(row) {
      this.currentMachiningPart = row
      this.$refs.materialSingleChoice.open()
    },
    materialSimpleChoice(info) {
      this.currentMachiningPart.baseMaterialId = info.id
      this.currentMachiningPart.baseMaterialName = info.name

      this.$refs.materialSingleChoice.close()
    }
  }
}
</script>
