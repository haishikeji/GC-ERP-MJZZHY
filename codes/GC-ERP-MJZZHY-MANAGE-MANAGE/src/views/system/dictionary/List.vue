<template>
  <div class="page-container">
    <el-row :gutter="2" style="height: 100%;">
      <el-col :span="12" style="height: 100%;">
        <!--数据字典类别列表 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label"><i class="el-icon-document" /> 数据字典类别</span>
            <el-button v-permission="['dictionary:categoty:add']" type="default" size="mini" @click="handleCategoryOpen(null)"><i class="fa fa-plus" aria-hidden="true" /> 新增</el-button>
            <el-button v-permission="['dictionary:category:delete']" type="danger" size="mini" :disabled="selectionCategories.length === 0" @click="handleCategoryBatchDelete"><i class="fa fa-trash" aria-hidden="true" /> 删除</el-button>
            <el-button type="success" size="mini" @click="loadData"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
          </div>
          <!--数据字典类别列表数据 开始-->
          <div class="el-table-container">
            <div>
              <el-table ref="categoryTable" :data="categories" size="mini" border highlight-current-row height="100%" row-key="id" default-expand-all header-row-class-name="list-header-row" row-class-name="list-row" :tree-props="{children: 'children'}" @select="handleTreeCheckChange" @current-change="handleCategoryCurrentChange" @select-all="(selection) => handleTreeSelectAll(selection, categories)">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column type="index" label="行号" width="40" align="center" />
                <el-table-column label="类别名称" prop="name" width="150" header-align="center" />
                <el-table-column label="类别编号" prop="code" width="150" align="center" />
                <el-table-column label="备注" prop="remark" header-align="center" />
                <el-table-column label="操作" width="40" align="center">
                  <template #default="scope">
                    <el-button v-permission="['dictionary:category:edit']" type="primary" size="mini" icon="el-icon-edit" circle @click="handleCategoryOpen(scope.row.id)" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <!--数据字典类别列表数据 结束-->
        </div>
      </el-col>
      <el-col :span="12" style="height: 100%;">
        <!--数据字典列表 开始-->
        <div class="page-container list-container">
          <!--操作按钮 开始-->
          <div class="list-btns-container">
            <span class="title-label"><i class="el-icon-document" /> 数据字典</span>
            <template v-if="currentCategory !== null">
              <el-button v-permission="['dictionary:add']" type="default" size="mini" @click="handleDictionaryCreate"><i class="fa fa-plus" aria-hidden="true" /> 新增</el-button>
              <el-button v-permission="['dictionary:delete']" type="danger" size="mini" :disabled="selectionDictionaries.length === 0" @click="handleDictionaryBatchDelete"><i class="fa fa-trash" aria-hidden="true" /> 删除</el-button>
              <el-button type="success" size="mini" @click="loadDictionaries"><i class="fa fa-refresh" aria-hidden="true" /> 刷新</el-button>
            </template>
          </div>
          <!--数据字典列表数据 开始-->
          <div class="el-table-container">
            <div>
              <el-table ref="table" show-summary :summary-method="()=>{return []}" :data="dictionaries" size="mini" border height="100%" header-row-class-name="list-header-row" row-class-name="list-row" @selection-change="handleDictionarySelectionChange">
                <el-table-column type="selection" width="40" align="center" />
                <el-table-column label="字典值" width="200" align="center">
                  <template #default="scope">
                    <div v-if="scope.row.editStatus">
                      <el-input v-model="scope.row.dictValue" size="mini" placeholder="字典值" />
                      <span class="required">*</span>
                    </div>
                    <div v-else>{{ scope.row.dictValue }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="字典编码" width="120" align="center">
                  <template #default="scope">
                    <div v-if="scope.row.editStatus">
                      <el-input v-model="scope.row.dictCode" size="mini" placeholder="字典编码" />
                    </div>
                    <div v-else>{{ scope.row.dictCode }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="备注" header-align="center">
                  <template #default="scope">
                    <div v-if="scope.row.editStatus">
                      <el-input v-model="scope.row.remark" size="mini" placeholder="备注" />
                    </div>
                    <div v-else>{{ scope.row.remark }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="排序" width="60" align="center">
                  <template #default="scope">
                    <div v-if="scope.row.editStatus">
                      <el-input-number v-model="scope.row.sort" size="mini" placeholder="排序" readonly :min="1" controls-position="right" />
                    </div>
                    <div v-else>{{ scope.row.sort }}</div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="64" align="center">
                  <template #default="scope">
                    <template v-if="scope.row.editStatus">
                      <el-button type="success" size="mini" icon="el-icon-finished" circle @click.stop.prevent="handleDictionarySave(scope.row)" />
                      <el-button v-if="scope.row.id" type="danger" size="mini" icon="el-icon-close" circle @click.stop.prevent="scope.row.editStatus = false" />
                    </template>
                    <template v-else>
                      <el-button v-permission="['dictionary:edit']" type="primary" size="mini" icon="el-icon-edit" circle @click.stop.prevent="scope.row.editStatus = true" />
                      <!--<el-button type="danger" size="mini" icon="el-icon-delete" circle @click.stop.prevent="handleDictionaryDelete(scope.row, scope.$index)" />-->
                    </template>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <!--数据字典列表数据 结束-->
        </div>
      </el-col>
    </el-row>

    <dialog-category-form ref="dialogDataDictionaryCategory" :parent="this" />
  </div>
</template>

<script>
import categoryApi from '@/api/sys/dataDictionaryCategory'
import dictionaryApi from '@/api/sys/dataDictionary'

import DialogCategoryForm from './DialogCategoryForm.vue'
import commmonKeepScrollMaxins from '@/views/common/commmonKeepScrollMaxins.js'
export default {
  name: 'DictionaryList',
  components: {
    DialogCategoryForm
  },
  mixins: [commmonKeepScrollMaxins],
  data() {
    return {
      categories: [],
      dictionaries: [],
      selectionCategories: [],
      selectionDictionaries: [],
      dictionaryEmptyData: {
        id: null,
        dataDictionaryCategoryIds: '',
        dataDictionaryCategoryId: '0',
        dictCode: '',
        dictValue: '',
        sort: '1',
        remark: '',
        editStatus: true
      },
      currentCategory: null,
      // tab切换 保持滚动条的混入属性
      list_ref_arr: ['table']
    }
  },
  created() {},
  mounted() {
    this.loadData()
  },
  activated() {
    // console.log('activi')
    // console.log(this.$route)
    // 因为神通模具,和思泰博不同,不用params
    if (this.$route.query.tab === true) {
      this.recopyScorllInActivate()
    }
  },
  methods: {
    /**
     * 加载数据
     */
    async loadData() {
      const categoryRes = await categoryApi.getList({})
      this.categories = categoryRes.data.list
      this.selectionCategories = []
      if (this.categories.length > 0) {
        if (this.currentCategory) {
          let cCategory = this.categories.find((item) => item.id === this.currentCategory.id)
          if (!cCategory) {
            cCategory = this.categories[0]
          }
          this.$refs.categoryTable.setCurrentRow(cCategory)
        } else {
          this.$refs.categoryTable.setCurrentRow(this.categories[0])
        }
      } else {
        this.currentCategory = null
        this.loadDictionaries()
      }
    },
    /**
     * 数据字典勾选checkbox
     */
    handleDictionarySelectionChange(selection) {
      this.selectionDictionaries = selection
    },
    /**
     * 根据当前的数据字典分类，加载数据字典
     */
    async loadDictionaries() {
      const categoryId = this.currentCategory ? this.currentCategory.id : 0
      const dictionaryRes = await dictionaryApi.getList({ categoryId })
      this.dictionaries = dictionaryRes.data.list
    },
    /**
     * 打开类别对话框 事件
     */
    handleCategoryOpen(id) {
      this.$refs.dialogDataDictionaryCategory.open(id)
    },
    /**
     * 批量删除类别 事件
     */
    handleCategoryBatchDelete() {
      const _this = this
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async () => {
          const res = await categoryApi.batchDelete(_this.selectionCategories)
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
     * 数据字典类别选中行改变时的事件
     * table组件调用setCurrentRow时，也会调用 current-change
     */
    handleCategoryCurrentChange(row) {
      this.currentCategory = row
      this.loadDictionaries()
    },
    /**
     * 添加数据字典 事件
     */
    handleDictionaryCreate() {
      // 此处必须进行深拷贝，否则添加的明细，将都指向了一个对象
      const _newDictionary = JSON.parse(JSON.stringify(this.dictionaryEmptyData))
      if (this.currentCategory.parentIds === '') {
        _newDictionary.dataDictionaryCategoryIds = this.currentCategory.id
      } else {
        _newDictionary.dataDictionaryCategoryIds = this.currentCategory.parentIds + ',' + this.currentCategory.id
      }
      _newDictionary.dataDictionaryCategoryId = this.currentCategory.id
      _newDictionary.sort = this.dictionaries.length + 1
      this.dictionaries.push(_newDictionary)
    },
    /**
     * 删除数据字典 事件
     */
    handleDictionaryDelete(data, index) {
      const _this = this
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async () => {
          if (data.id !== '' && data.id !== '0') {
            const res = await dictionaryApi.deleteById(data.id)
            if (res.code === 200) {
              _this.$message({
                message: res.message,
                type: 'success'
              })
            }
          }
          _this.$delete(_this.dictionaries, index)
        })
        .catch(() => {
          _this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    /**
     * 批量删除数据字典 事件
     */
    handleDictionaryBatchDelete() {
      const _this = this
      const unSelection = _this.dictionaries.filter(function (item) {
        return (
          _this.selectionDictionaries.filter(function (item2) {
            return item.id === item2.id
          }).length === 0
        )
      })
      const count = unSelection.filter(function (item) {
        return item.editStatus
      }).length
      if (count > 0) {
        _this.$message({
          type: 'warning',
          message: '有尚未保存的数据，请先保存'
        })
        return
      }
      _this
        .$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        .then(async () => {
          const res = await dictionaryApi.batchDelete(_this.selectionDictionaries)
          if (res.code === 200) {
            _this.$message({
              message: res.message,
              type: 'success'
            })
            _this.loadDictionaries()
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
     * 保存数据字典 事件
     */
    async handleDictionarySave(data) {
      if (data.dictValue === '') {
        this.$message.error('数据字典值不能为空')
        return
      }
      const saveRes = await dictionaryApi.save(data)
      if (saveRes.code === 200) {
        this.$message({
          message: saveRes.message,
          type: 'success'
        })
        this.loadDictionaries()
      } else {
        this.$message.error('操作失败')
      }
    },
    handleTreeCheckChange(selection, row) {
      // 判断是选中还是取消选中
      const value = !(selection.findIndex((i) => i === row) < 0)
      if (row.children != null && row.children.length > 0) this.childrenSelect(row.children, value)
      if (value) {
        const i = this.selectionCategories.findIndex((i) => i === row)
        if (i < 0) {
          this.selectionCategories.push(row)
        }
      } else {
        const i = this.selectionCategories.findIndex((i) => i === row)
        if (i >= 0) {
          this.selectionCategories.splice(i, 1)
        }
      }
    },
    handleTreeSelectAll: function (selections, data) {
      // 判断 selections 是否是第一层数据 如果不是 则全部取消选中
      const i = data.findIndex((i) => i === (selections.length > 0 ? selections[0] : null))
      if (i < 0) {
        const length = selections.length
        for (let i = 0; i < length; i++) {
          this.$refs.categoryTable.toggleRowSelection(selections[0], false)
        }
        this.selectionCategories = []
      } else {
        selections.forEach((selection) => {
          const i = this.selectionCategories.findIndex((i) => i === selection)
          if (i < 0) {
            this.selectionCategories.push(selection)
          }
          if (selection.children != null && selection.children.length > 0) {
            this.childrenSelect(selection.children, true)
          }
        })
      }
    },
    childrenSelect: function (children, value) {
      children.forEach((child) => {
        this.$refs.categoryTable.toggleRowSelection(child, value)
        if (value) {
          const i = this.selectionCategories.findIndex((i) => i === child)
          if (i < 0) {
            this.selectionCategories.push(child)
          }
        } else {
          const i = this.selectionCategories.findIndex((i) => i === child)
          if (i >= 0) {
            this.selectionCategories.splice(i, 1)
          }
        }
        if (child.children != null && child.children.length > 0) this.childrenSelect(child.children, value)
      })
    }
  }
}
</script>
