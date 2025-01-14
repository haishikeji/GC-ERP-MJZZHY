<template>
  <el-dialog v-el-drag-dialog :visible="visible" :width="width" :modal="true" :modal-append-to-body="true" :append-to-body="true" :show-close="false" custom-class="dialog-form">
    <div slot="title" class="dialog-title-container">
      <span class="title-label"><i class="el-icon-document" /> 采购供应商选择</span>
      <i class="el-icon-error" @click="close" />
    </div>
    <!-- 功能按钮 -->
    <div v-if="multiple" class="form-btns-container">
      <el-button type="success" size="mini" @click="handleMultipleSelected"><i class="fa fa-floppy-o" aria-hidden="true" /> 确定选择</el-button>
    </div>
    <div class="dialog-list-container">
      <el-table :data="list" size="mini" border height="400px" header-row-class-name="list-header-row" row-class-name="list-row" @selection-change="handleSelectionChange">
        <el-table-column v-if="multiple" type="selection" width="40" align="center" />
        <el-table-column type="index" label="行号" width="46" align="center" />
        <el-table-column label="供应商简称" prop="shortName" width="150" align="center" />
        <el-table-column label="供应商名称" prop="name" align="center" />
        <el-table-column v-if="!multiple" label="操作" width="50" align="center">
          <template #default="scope">
            <el-button type="success" size="mini" icon="el-icon-finished" circle @click="handleSimpleSelected(scope.row)" />
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="pagination-container">
      <el-pagination background :current-page="query.pn" :page-size="query.pageSize" :total="query.total" :page-sizes="$pageSizeArr" layout="total, sizes, prev, pager, next, jumper" @size-change="handlePageSizeChange" @current-change="handlePageChange" />
    </div>
  </el-dialog>
</template>
<script>
import api from '@/api/biz/company'
export default {
  name: 'DialogCustomerChoice',
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
    }
  },

  data: function() {
    return {
      visible: false,
      list: [],
      selection: [],
      query: {
        // 合作伙伴分类：1、客户；2、供应商；3、外协商
        category: '2',
        name: '',
        pn: 1, // 页码
        pageSize: this.$defaulPageSize, // 每页条数
        total: 1
      }
    }
  },
  mounted() {},
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
    /**
     * 加载数据
     */
    async loadData() {
      const res = await api.getList(this.query)
      this.list = res.data.list
      this.query.total = res.data.total
    },
    /**
     * 单选事件
     */
    handleSimpleSelected(row) {
      if (this.simpleSelected) {
        this.simpleSelected(row)
      }
    },
    /**
     * 多选事件
     */
    handleMultipleSelected() {
      if (this.multipleSelected) {
        this.multipleSelected(this.selection)
      }
    },
    /** 页码改变事件 */
    handlePageSizeChange: function(pageSize) {
      this.query.pn = 1
      this.query.pageSize = pageSize
      this.loadData()
    },
    handlePageChange: function(pn) {
      this.query.pn = pn
      this.loadData()
    },
    handleSelectionChange: function(selection) {
      this.selection = selection
    }
  }
}
</script>
