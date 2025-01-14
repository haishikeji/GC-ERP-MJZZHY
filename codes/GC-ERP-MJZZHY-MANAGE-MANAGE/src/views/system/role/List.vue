<template>
  <!--列表 开始-->
  <div class="page-container">
    <el-row :gutter="2" style="height: 100%;">
      <el-col :span="6" style="height: 100%;">
        <!--角色列表 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label">角色</span>
            <el-button v-permission="['role:add']" type="default" size="mini" @click="handleAdd"><i class="fa fa-plus" aria-hidden="true" /> 新增</el-button>
            <el-button v-permission="['role:delete']" v-if="selectionRoles.length > 0" type="danger" size="mini" @click="handleBatchDelete"><i class="fa fa-trash" aria-hidden="true" /> 删除</el-button>
            <el-button type="success" size="mini" @click="loadData"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
          </div>
          <!--角色列表数据 开始-->
          <div class="el-table-container">
            <div>
              <el-table ref="roleTable" :data="roles" size="mini" border highlight-current-row height="100%" header-row-class-name="list-header-row" row-class-name="list-row" @selection-change="handleRoleSelectionChange" @current-change="handleRoleCurrentChange">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column type="index" label="序号" width="40" align="center" />
                <el-table-column label="角色名称" width="150" align="center">
                  <template #default="item">
                    <div v-if="item.row.editStatus">
                      <el-input v-model="item.row.name" size="mini" placeholder="角色名称" />
                    </div>
                    <div v-else>{{ item.row.name }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="备注" header-align="center">
                  <template #default="item">
                    <div v-if="item.row.editStatus">
                      <el-input v-model="item.row.remark" size="mini" placeholder="备注" />
                    </div>
                    <div v-else>{{ item.row.remark }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="64" align="center">
                  <template #default="scope">
                    <template v-if="scope.row.editStatus">
                      <el-button type="success" size="mini" icon="el-icon-finished" circle @click.stop.prevent="handleSave(scope.row)" />
                      <el-button type="danger" size="mini" icon="el-icon-close" circle @click.stop.prevent="scope.row.editStatus = false" />
                    </template>
                    <template v-else>
                      <el-button v-permission="['role:edit']" type="primary" size="mini" icon="el-icon-edit" circle @click.stop.prevent="scope.row.editStatus = true" />
                    </template>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <!--角色列表数据 结束-->
        </div>
      </el-col>
      <el-col :span="6" style="height: 100%;">
        <!--菜单列表数据 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label">菜单配置</span>
            <el-button v-permission="['role:menu:save']" type="default" size="mini" @click="handleSavePermissionSetting('menu')"><i class="fa fa-floppy-o" aria-hidden="true" /> 保存</el-button>
            <el-button type="success" size="mini" @click="loadRolePermissions('menu')"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
          </div>
          <div class="el-table-container">
            <div>
              <el-table ref="menuTable" :data="allMenus" size="mini" border height="100%" row-key="id" default-expand-all header-row-class-name="list-header-row" row-class-name="list-row" :tree-props="{children: 'children'}" @select="(selection, row) => handleTreeCheckChange(selection, row, 'menuTable', selectionMenus)" @selection-change="handleMenuSelectionChange" @select-all="(selection) => handleTreeSelectAll(selection, allMenus, 'menuTable', selectionMenus)">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column label="菜单名" prop="name" width="200" header-align="center" />
                <el-table-column label="备注" prop="remark" header-align="center" />
              </el-table>
            </div>
          </div>
        </div>
        <!--菜单列表数据 结束-->
      </el-col>
      <el-col :span="6" style="height: 100%;">
        <!--权限列表数据 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label">权限配置</span>
            <el-button v-permission="['role:permission:save']" type="default" size="mini" @click="handleSavePermissionSetting('permission')"><i class="fa fa-floppy-o" aria-hidden="true" /> 保存</el-button>
            <el-button type="success" size="mini" @click="loadRolePermissions('permission')"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
          </div>
          <div class="el-table-container">
            <div>
              <el-table ref="permissionTable" :data="allPermissions" size="mini" border height="100%" row-key="id" default-expand-all header-row-class-name="list-header-row" row-class-name="list-row" :tree-props="{children: 'children'}" @select="(selection, row) => handleTreeCheckChange(selection, row, 'permissionTable', selectionPermissions)" @selection-change="handlePermissionSelectionChange" @select-all="(selection) => handleTreeSelectAll(selection, allPermissions, 'permissionTable', selectionPermissions)">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column label="权限名" prop="name" width="200" header-align="center" />
                <el-table-column label="备注" prop="remark" header-align="center" />
              </el-table>
            </div>
          </div>
        </div>
        <!--权限列表数据 结束-->
      </el-col>
      <el-col :span="6" style="height: 100%;">
        <!--权限列表数据 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label">员工</span>
            <el-button v-permission="['role:people:add']" type="default" size="mini" @click="showUserEmployeeDialog"><i class="fa fa-plus" aria-hidden="true" /> 添加员工</el-button>
            <el-button v-permission="['role:people:delete']" v-if="selectionUsers.length > 0" type="danger" size="mini" @click="handleUserBatchDelete"><i class="fa fa-trash" aria-hidden="true" /> 删除</el-button>
            <el-button type="success" size="mini" @click="loadRoleUsers()"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
          </div>
          <div class="el-table-container">
            <div>
              <el-table ref="roleUserTable" :data="roleUsers" size="mini" border height="100%" row-key="id" header-row-class-name="list-header-row" row-class-name="list-row" @selection-change="handleUserSelectionChange">
                <el-table-column type="selection" width="40" align="center" />
                <!-- <el-table-column label="员工编号" prop="employeeCode" width="100" header-align="center" /> -->
                <el-table-column label="员工姓名" prop="employeeName" width="100" header-align="center" />
                <el-table-column label="电话号码" prop="phone" width="100" header-align="center" />
              </el-table>
            </div>
          </div>
        </div>
        <!--权限列表数据 结束-->
      </el-col>
    </el-row>
    <dialog-employee-choice ref="employeeMultiChoice" :parent="this" :multiple="true" :multiple-selected="employeeMultiChoiceHandler" :width="'800px'" />
  </div>
  <!--列表 结束-->
</template>

<script>
import roleApi from '@/api/sys/sysRole'
// import permissionApi from '@/api/sys/sysPermission'
import rolePermissionApi from '@/api/sys/sysRolePermission'
import roleUserApi from '@/api/sys/sysRoleUser'
import DialogEmployeeChoice from '@/views/dialogs/DialogUserChoice.vue'

export default {
  name: 'RoleList',
  // 注册子组件
  components: {
    DialogEmployeeChoice
  },

  data() {
    return {
      // 角色列表
      roles: [],
      // 当前角色配置的菜单id集合
      menuIds: [],
      // 所有菜单
      allMenus: [],
      // 当前角色配置的权限id集合
      permissionIds: [],
      // 所有权限
      allPermissions: [],
      roleUsers: [],
      // 列表中选中的行对象集合
      selectionRoles: [],
      selectionMenus: [],
      selectionUsers: [],
      selectionPermissions: [],
      roleEmptyData: {
        id: null,
        name: '',
        remark: '',
        editStatus: true
      },
      currentSysRole: null
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    /** 页面事件 start */
    /**
     * 请求数据
     */
    async loadData() {
      const res = await roleApi.getList()
      if (res.code === 200) {
        this.roles = res.data.list
        this.allMenus = res.data.menus
        this.allPermissions = res.data.permissions
        this.selectionMenus = []
        this.selectionPermissions = []
        if (this.roles.length > 0) {
          if (this.currentSysRole) {
            let sysRole = this.roles.find((item) => item.id === this.currentSysRole.id)
            if (!sysRole) {
              sysRole = this.roles[0]
            }
            this.$refs.roleTable.setCurrentRow(sysRole)
          } else {
            this.$refs.roleTable.setCurrentRow(this.roles[0])
          }
        }
      }
    },
    /**
     * 添加Role 事件
     */
    handleAdd() {
      // 此处必须进行深拷贝，否则添加的明细，将都指向了一个对象
      const _newRole = JSON.parse(JSON.stringify(this.roleEmptyData))
      this.roles.push(_newRole)
    },
    /**
     * 批量删除 事件
     */
    handleBatchDelete() {
      const _this = this
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async () => {
          const res = await roleApi.batchDelete(_this.selectionRoles)
          if (res.code === 200) {
            _this.$message({
              message: res.message,
              type: 'success'
            })
            _this.loadData()
          } else this.$message.error(res.message)
        })
        .catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    /**
     * 保存角色 事件
     */
    async handleSave(data) {
      if (data.userName === '') {
        this.$message.error('角色名称不能为空')
        return
      }
      const saveRes = await roleApi.save(data)
      if (saveRes.code === 200) {
        this.$message({
          message: saveRes.message,
          type: 'success'
        })
        this.loadData()
      } else {
        this.$message.error('操作失败')
      }
    },
    /**
     * 角色勾选checkbox
     */
    handleRoleSelectionChange(selection) {
      this.selectionRoles = selection
    },
    /**
     * 角色选中行改变时的事件
     * table组件调用setCurrentRow时，也会调用 current-change
     */
    handleRoleCurrentChange(row) {
      this.currentSysRole = row
      this.loadRolePermissions()
      this.loadRoleUsers()
    },
    /**
     * 根据当前选中的角色，获取对应的 菜单/权限
     */
    async loadRolePermissions(type) {
      const _this = this
      if (_this.currentSysRole) {
        const res = await rolePermissionApi.getList({ sysRoleId: _this.currentSysRole.id })
        if (!type || type === 'menu') {
          _this.menuIds = res.data.menuIds
          _this.$refs.menuTable.clearSelection()
          _this.selectionMenus = []
          _this.recursionPermissions('menu', _this.allMenus)
        }
        if (!type || type === 'permission') {
          _this.permissionIds = res.data.permissionIds
          _this.$refs.permissionTable.clearSelection()
          _this.selectionPermissions = []
          _this.recursionPermissions('permission', _this.allPermissions)
        }
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
    handleUserSelectionChange(selection) {
      this.selectionUsers = selection
    },
    /**
     * 保存菜单的配置
     */
    async handleSavePermissionSetting(type) {
      const _this = this
      const data = {
        type,
        sysRoleId: _this.currentSysRole.id,
        permissions: []
      }
      if (type === 'menu') {
        data.permissions = _this.selectionMenus
      }
      if (type === 'permission') {
        data.permissions = _this.selectionPermissions
      }
      const saveRes = await rolePermissionApi.save(data)
      if (saveRes.code === 200) {
        this.$message({
          message: saveRes.message,
          type: 'success'
        })
      } else {
        this.$message.error('操作失败')
      }
    },
    /**
     * 父节点选中事件
     */
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
    /**
     * 全选事件
     */
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
      // console.log(this.selectionMenus, this.selectionPermissions)
    },
    /**
     * 子节点选择
     */
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
    },
    /**
     * 递归判断菜单是否配置，如果配置，则默认选中
     */
    async recursionPermissions(type, permissions) {
      const _this = this
      if (type === 'menu') {
        permissions.forEach((item) => {
          if (_this.menuIds.includes(item.id)) {
            _this.$refs.menuTable.toggleRowSelection(item, true)
            const i = _this.selectionMenus.findIndex((i) => i === item)
            if (i < 0) {
              _this.selectionMenus.push(item)
            }
            // _this.selectionMenus.push(item)
            if (item.children.length > 0) {
              _this.recursionPermissions(type, item.children)
            }
          }
        })
        return
      }
      if (type === 'permission') {
        permissions.forEach((item) => {
          if (_this.permissionIds.includes(item.id)) {
            _this.$refs.permissionTable.toggleRowSelection(item, true)
            // _this.selectionPermissions.push(item)
            const i = _this.selectionPermissions.findIndex((i) => i === item)
            if (i < 0) {
              _this.selectionPermissions.push(item)
            }
            if (item.children.length > 0) {
              _this.recursionPermissions(type, item.children)
            }
          }
        })
        return
      }
      return
    },
    /** 角色用户关系表 */
    async loadRoleUsers() {
      const res = await roleUserApi.getList({ sysRoleId: this.currentSysRole.id })
      if (res.code === 200) {
        this.roleUsers = res.data.list
      }
    },
    showUserEmployeeDialog() {
      this.$refs.employeeMultiChoice.open()
    },
    async handleUserBatchDelete() {
      const _this = this
      // console.log(_this.selectionCategories)
      const confirmRes = await this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          return true
        })
        .catch(() => {
          return false
        })
      if (confirmRes) {
        const res = await roleUserApi.batchDelete(_this.selectionUsers.map((l) => l.id))
        if (res.code === 200) {
          _this.$message.success(res.message)
          _this.loadData()
        } else this.$message.error(res.message)
      } else {
        _this.$message.info('已取消删除')
      }
    },
    async employeeMultiChoiceHandler(_infos) {
      // console.log(_infos)
      _infos.forEach((l) => {
        const index = this.roleUsers.findIndex((v) => v.userId === l.id)
        if (index < 0) {
          const newItem = {}
          newItem.userId = l.id
          newItem.sysUserId = l.id
          newItem.sysRoleId = this.currentSysRole.id
          newItem.employeeId = l.employeeId
          newItem.employeeName = l.employeeName
          newItem.employeeCode = l.employeeCode
          newItem.phone = l.phone
          this.roleUsers.push(newItem)
        }
      })
      const res = await roleUserApi.saveBatch(this.roleUsers)
      if (res.code === 200) {
        this.loadRoleUsers()
      } else {
        this.$message.error(res.message)
      }
      this.$refs.employeeMultiChoice.close()
    }
  }
}
</script>
