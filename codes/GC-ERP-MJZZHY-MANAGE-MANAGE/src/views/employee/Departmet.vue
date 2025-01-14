<template>
  <el-dialog v-el-drag-dialog :visible="visible" :width="width" :modal="true" :modal-append-to-body="false" :show-close="false" custom-class="dialog-form">
    <div slot="title" class="dialog-title-container">
      <span class="title-label"><i class="el-icon-document" /> 部门选择</span>
      <i class="el-icon-error" @click="close" />
    </div>
    <!-- 功能按钮 -->
    <div v-if="multiple" class="form-btns-container">
      <el-button type="success" size="mini" @click="handleMultipleSelected">
        <i class="fa fa-floppy-o" aria-hidden="true" /> 确定选择
      </el-button>
    </div>
    <div class="dialog-list-container" style="height: 450px;">
      <el-row :gutter="2" style="height: 100%;">
        <el-col :span="24" style="height: 100%;">
          <!--数据字典列表 开始-->
          <div class="page-container list-container">
            <!--操作按钮 开始-->
            <div class="list-btns-container">
              <span class="title-label"><i class="el-icon-document" /> 部门</span>
            </div>
            <!--数据字典列表数据 开始-->
            <div class="el-table-container">
              <div>
                <el-table ref="departmentTable" :data="departments" size="mini" border highlight-current-row height="100%" row-key="id" header-row-class-name="list-header-row" row-class-name="list-row" default-expand-all :tree-props="{children: 'children'}" @select="handleTreeCheckChange" @select-all="(selection) => handleTreeSelectAll(selection, departments)" @current-change="handleDepartmentCurrentChange">
                  <el-table-column type="index" label="序号" width="40" align="center" />
                  <el-table-column label="部门名称" prop="name" align="center" />
                  <el-table-column label="操作" width="40" align="center">
                    <template #default="scope">
                      <el-button type="success" size="mini" icon="el-icon-finished" circle @click="handleSimpleSelected(scope.row)" />
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
            <!--数据字典列表数据 结束-->
          </div>
        </el-col>
      </el-row>
    </div>
  </el-dialog>
</template>
<script>
/**
 * 2022-5-53
 * 仿照部门的list页面
 */
import departmentApi from '@/api/biz/department'
export default {
  name: 'EmployeeDeparmenttCom',
  components: {},
  props: {
    parent: {
      type: Object,
      default: () => null
    },
    width: {
      type: String,
      default: '800px'
    },
    multiple: {
      type: Boolean,
      default: () => false
    },
    simpleSelected: {
      type: Function,
      default: null
    },
    multipleSelected: {
      type: Function,
      default: null
    },
    defaultDepartment: {
      type: String,
      default: null
    }
  },
  data: function () {
    return {
      // 显隐标识
      visible: false,
      departments: [],
      employees: [],
      // 查询请求携带的参数
      query: {
        departmentId: null,
        name: '',
        total: 0,
        pageSize: this.$defaulPageSize,
        pn: 1
      },
      selectionDepartments: [],
      selectionEmployees: [],
      currentDepartment: null
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
     * 对话框关闭 事件
     */
    close() {
      this.visible = false
    },

    async loadData() {
      // console.log('v2')
      const departmentRes = await departmentApi.getList({})
      this.departments = departmentRes.data.list
      this.selectionDepartments = []
      if (this.departments.length > 0) {
        if (this.defaultDepartment) {
          // let currentDepartment = this.departments.find(item => item.name === this.defaultDepartment)
          let currentDepartment = this.treeDefaultChoice(this.departments, this.defaultDepartment)
          if (!currentDepartment) {
            currentDepartment = this.departments[0]
          }
          this.$refs.departmentTable.setCurrentRow(currentDepartment)
        } else if (this.currentDepartment) {
          let currentDepartment = this.departments.find((item) => item.id === this.currentDepartment.id)
          if (!currentDepartment) {
            currentDepartment = this.departments[0]
          }
          this.$refs.departmentTable.setCurrentRow(currentDepartment)
        } else {
          this.$refs.departmentTable.setCurrentRow(this.departments[0])
        }
      } else {
        this.currentDepartment = null
      }
    },

    /**
     * 加载数据
     */
    async loadDataOld() {
      const departmentRes = await departmentApi.getListAndEmployees({ departmentId: this.currentDepartment.id })
      this.departments = departmentRes.data.list
      if (this.departments.length > 0) {
        this.$refs.departmentTable.setCurrentRow(this.departments[0])
      }
    },
    /**
     * 列表checkbox列选择 事件
     */
    handleSelectionChange(selection) {
      this.selection = selection
    },
    /**
     * 搜索 事件
     */
    handleSearch() {
      this.loadData()
    },
    handleTreeCheckChange(selection, row) {
      // 判断是选中还是取消选中
      const value = !(selection.findIndex((i) => i === row) < 0)
      if (row.children != null && row.children.length > 0) this.childrenSelect(row.children, value)
      if (value) {
        const i = this.selectionDepartments.findIndex((i) => i === row)
        if (i < 0) {
          this.selectionDepartments.push(row)
        }
      } else {
        const i = this.selectionDepartments.findIndex((i) => i === row)
        if (i >= 0) {
          this.selectionDepartments.splice(i, 1)
        }
      }
    },
    handleTreeSelectAll: function (selections, data) {
      // 判断 selections 是否是第一层数据 如果不是 则全部取消选中
      const i = data.findIndex((i) => i === (selections.length > 0 ? selections[0] : null))
      if (i < 0) {
        const length = selections.length
        for (let i = 0; i < length; i++) {
          this.$refs.departmentTable.toggleRowSelection(selections[0], false)
        }
        this.selectionDepartments = []
      } else {
        selections.forEach((selection) => {
          const i = this.selectionDepartments.findIndex((i) => i === selection)
          if (i < 0) {
            this.selectionDepartments.push(selection)
          }
          if (selection.children != null && selection.children.length > 0) {
            this.childrenSelect(selection.children, true)
          }
        })
      }
    },
    childrenSelect: function (children, value) {
      children.forEach((child) => {
        this.$refs.departmentTable.toggleRowSelection(child, value)
        if (child.children != null && child.children.length > 0) this.childrenSelect(child.children, value)
        if (value) {
          const i = this.selectionDepartments.findIndex((i) => i === child)
          if (i < 0) {
            this.selectionDepartments.push(child)
          }
        } else {
          const i = this.selectionDepartments.findIndex((i) => i === child)
          if (i >= 0) {
            this.selectionDepartments.splice(i, 1)
          }
        }
      })
    },
    treeDefaultChoice(departments, defaultDepartment) {
      let currentDepartment = null
      departments.forEach((l) => {
        currentDepartment = currentDepartment == null && l.name === defaultDepartment ? l : currentDepartment
        if (l.children != null) {
          currentDepartment = currentDepartment == null ? this.treeDefaultChoice(l.children, defaultDepartment) : currentDepartment
        }
      })
      return currentDepartment
    },
    /**
     * 部门选中行改变时的事件
     * table组件调用setCurrentRow时，也会调用 current-change
     */
    handleDepartmentCurrentChange(row) {
      this.currentDepartment = row
    },
    handleSimpleSelected(row) {
      this.$emit('callback', row)
      this.close()
    }
  }
}
</script>
