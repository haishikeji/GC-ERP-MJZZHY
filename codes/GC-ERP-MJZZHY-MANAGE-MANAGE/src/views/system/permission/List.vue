<template>
  <div class="page-container">
    <el-row :gutter="2" style="height: 100%;">
      <el-col :span="17" style="height: 100%;">
        <!--菜单列表 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label"><i class="el-icon-document" /> 菜单列表</span>
            <el-button v-permission="['menu:add']" type="default" size="mini" @click="handleDialogPermissionOpen('menu', null)"><i class="fa fa-plus" aria-hidden="true" /> 新增</el-button>
            <el-button v-permission="['menu:delete']" type="danger" size="mini" :disabled="selectionMenus.length === 0" @click="handleMenuBatchDelete"><i class="fa fa-trash" aria-hidden="true" /> 删除</el-button>
            <el-button type="success" size="mini" @click="loadData"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
          </div>
          <!--菜单列表数据 开始-->
          <div class="el-table-container">
            <div>
              <el-table ref="menuTable" show-summary :summary-method="()=>{return []}" :data="menus" size="mini" border highlight-current-row height="100%" row-key="id" default-expand-all header-row-class-name="list-header-row" row-class-name="list-row" :tree-props="{ children: 'children' }" @select="(selection, row) => handleTreeCheckChange(selection, row, 'menuTable', selectionMenus)" @selection-change="handleMenuSelectionChange" @select-all="selection => handleTreeSelectAll(selection, menus, 'menuTable', selectionMenus)">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column type="index" label="行号" width="40" align="center" />
                <el-table-column label="菜单名称" prop="name" width="160" header-align="center" />
                <el-table-column label="路由名称" prop="routeName" width="140" header-align="center" />
                <el-table-column label="路由路径" prop="routePath" width="140" header-align="center" />
                <el-table-column label="组件路径" prop="componentPath" width="160" header-align="center" />
                <el-table-column label="菜单图标" prop="icon" width="80" header-align="center" />
                <el-table-column label="面包屑导航重定向地址" prop="redirectUrl" header-align="center" />
                <el-table-column label="是否隐藏" prop="isHidden" width="60" align="center">
                  <template #default="scope">
                    {{ scope.row.isHidden === 1 ? '是' : '否' }}
                  </template>
                </el-table-column>
                <el-table-column label="始终显示" prop="alwaysShow" width="60" align="center">
                  <template #default="scope">
                    {{ scope.row.alwaysShow === 1 ? '是' : '否' }}
                  </template>
                </el-table-column>
                <el-table-column label="排序" prop="sort" width="40" align="center" />
                <el-table-column label="操作" width="40" align="center">
                  <template #default="scope">
                    <el-button v-permission="['menu:edit']" type="primary" size="mini" icon="el-icon-edit" circle @click="handleDialogPermissionOpen('menu', scope.row.id)" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <!--菜单列表数据 结束-->
        </div>
      </el-col>
      <el-col :span="7" style="height: 100%;">
        <!--菜单列表 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label"><i class="el-icon-document" /> 权限列表</span>
            <el-button v-permission="['menu:permission:add']" type="default" size="mini" @click="handleDialogPermissionOpen('permission', '0')"><i class="fa fa-plus" aria-hidden="true" /> 新增</el-button>
            <el-button v-permission="['menu:permission:delete']" type="danger" size="mini" :disabled="selectionPermissions.length === 0" @click="handlePermissionBatchDelete"><i class="fa fa-trash" aria-hidden="true" /> 删除</el-button>
            <el-button type="success" size="mini" @click="loadData"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
          </div>
          <!--菜单列表数据 开始-->
          <div class="el-table-container">
            <div>
              <el-table ref="permissionTable" show-summary :summary-method="()=>{return []}" :data="permissions" size="mini" border highlight-current-row height="100%" row-key="id" default-expand-all header-row-class-name="list-header-row" row-class-name="list-row" :tree-props="{ children: 'children' }" @select="(selection, row) => handleTreeCheckChange(selection, row, 'permissionTable', selectionPermissions)" @selection-change="handlePermissionSelectionChange" @select-all="selection => handleTreeSelectAll(selection, permissions, 'permissionTable', selectionPermissions)">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column type="index" label="行号" width="40" align="center" />
                <el-table-column label="权限名称" prop="name" header-align="center" />
                <el-table-column label="权限编码" prop="code" width="150" header-align="center" />
                <el-table-column label="排序" prop="sort" width="40" align="center" />
                <el-table-column label="操作" width="40" align="center">
                  <template #default="scope">
                    <el-button v-permission="['menu:permission:edit']" type="primary" size="mini" icon="el-icon-edit" circle @click="handleDialogPermissionOpen('permission', scope.row.id)" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <!--菜单列表数据 结束-->
        </div>
      </el-col>
    </el-row>
    <dialog-permission ref="dialogPermission" :parent="this" />
  </div>
</template>

<script>
import api from '@/api/sys/sysPermission'

import DialogPermission from './DialogPermission.vue'
import commmonKeepScrollMaxins from '@/views/common/commmonKeepScrollMaxins.js'
export default {
  name: 'PermissionList',
  components: {
    DialogPermission
  },
  mixins: [commmonKeepScrollMaxins],
  data() {
    return {
      menus: [],
      permissions: [],
      selectionMenus: [],
      selectionPermissions: [],
      // tab切换 保持滚动条的混入属性
      list_ref_arr: ['menuTable', 'permissionTable']
    }
  },
  created() {},
  mounted() {
    this.loadData()
  },
  activated() {
    if (this.$route.query.tab === true) {
      this.recopyScorllInActivate()
    }
  },
  methods: {
    /**
     * 加载数据
     */
    async loadData() {
      this.menus = []
      this.permissions = []
      this.selectionMenus = []
      this.selectionPermissions = []
      const res = await api.getList({})
      if (res.data.menus.length > 0) {
        this.menus = res.data.menus
      }
      if (res.data.permissions.length > 0) {
        this.permissions = res.data.permissions
      }
    },
    /**
     * 菜单勾选checkbox
     */
    handleMenuSelectionChange(selection) {
      // this.selectionMenus = selection
    },
    /**
     * 权限勾选checkbox
     */
    handlePermissionSelectionChange(selection) {
      // this.selectionPermissions = selection
    },
    /**
     * 打开类别对话框 事件
     */
    handleDialogPermissionOpen(type, id) {
      this.$refs.dialogPermission.open(type, id)
    },
    /**
     * 批量删除菜单 事件
     */
    handleMenuBatchDelete() {
      const _this = this
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async () => {
          const res = await api.batchDelete(_this.selectionMenus)
          if (res.code === 200) {
            _this.$message({
              message: res.message,
              type: 'success'
            })
            _this.loadData()
          }
        })
        .catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    /**
     * 批量删除权限 事件
     */
    handlePermissionBatchDelete() {
      const _this = this
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async () => {
          const res = await api.batchDelete(_this.selectionPermissions)
          if (res.code === 200) {
            _this.$message({
              message: res.message,
              type: 'success'
            })
            _this.loadData()
          }
        })
        .catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    handleTreeCheckChange(selection, row, refName, selectionArr) {
      // 判断是选中还是取消选中
      const value = !(selection.findIndex((i) => i === row) < 0)
      if (row.children != null && row.children.length > 0) this.childrenSelect(row.children, value, refName, selectionArr)
      if (value) {
        const i = selectionArr.findIndex((i) => i === row)
        if (i < 0) {
          selectionArr.push(row)
        }
      } else {
        const i = selectionArr.findIndex((i) => i === row)
        if (i >= 0) {
          selectionArr.splice(i, 1)
        }
      }
    },
    handleTreeSelectAll: function (selections, data, refName, selectionArr) {
      // 判断 selections 是否是第一层数据 如果不是 则全部取消选中
      const i = data.findIndex((i) => i === (selections.length > 0 ? selections[0] : null))
      if (i < 0) {
        const length = selections.length
        for (let i = 0; i < length; i++) {
          this.$refs[refName].toggleRowSelection(selections[0], false)
        }
        selectionArr.splice(0, selectionArr.length)
      } else {
        selections.forEach((selection) => {
          const i = selectionArr.findIndex((i) => i === selection)
          if (i < 0) {
            selectionArr.push(selection)
          }
          if (selection.children != null && selection.children.length > 0) {
            this.childrenSelect(selection.children, true, refName, selectionArr)
          }
        })
      }
    },
    childrenSelect: function (children, value, refName, selectionArr) {
      children.forEach((child) => {
        if (value) {
          const i = selectionArr.findIndex((i) => i === child)
          if (i < 0) {
            selectionArr.push(child)
          }
        } else {
          const i = selectionArr.findIndex((i) => i === child)
          if (i >= 0) {
            selectionArr.splice(i, 1)
          }
        }
        this.$refs[refName].toggleRowSelection(child, value)
        if (child.children != null && child.children.length > 0) this.childrenSelect(child.children, value, refName, selectionArr)
      })
    }
  }
}
</script>
