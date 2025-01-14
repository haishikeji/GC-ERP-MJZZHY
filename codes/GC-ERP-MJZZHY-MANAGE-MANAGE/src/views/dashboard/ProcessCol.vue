<template>
  <div>
    <el-row>
      <template v-for="(temp, index) in makeProcessArr()">
        <el-tooltip :key="index" class="item" effect="dark" :content="temp.processName" placement="top">
          <el-button circle size="mini" :style="{background:temp.background,color:'white'}">
            <i v-if="temp.status !==''" class="el-icon-check" />
            <i v-else class="el-icon-warning-outline" style="visibility:hidden" />
          </el-button>
        </el-tooltip>
      </template>
    </el-row>
  </div>
</template>
<script>
/**
 * 原型显示工序,
 * 有入库高店工序颜色，否则灰色
 * 当工序全部结束的时候,显示对号
 */
export default {
  props: {
    item: {
      type: Object,
      default: Object
    }
  },
  methods: {
    makeProcessArr(val) {
      var processArr = []

      var count = this.item.processCount
      var processNameArr = []
      var inStoreStatusArr = []
      var processColorArr = []
      var statusArr = []
      var nameArr = []

      // 工序
      if (this.item.processName !== null && this.item.processName !== undefined && this.item.processName !== '') {
        processNameArr = this.item.processName.split(',')
      }

      // 入库状态
      if (this.item.inStoreStatus !== null && this.item.inStoreStatus !== undefined && this.item.inStoreStatus !== '') {
        inStoreStatusArr = this.item.inStoreStatus.split(',')
      } else {
        inStoreStatusArr = new Array(processNameArr.length)
      }

      // 工序颜色
      if (this.item.processColor !== null && this.item.processColor !== undefined && this.item.processColor !== '') {
        processColorArr = this.item.processColor.split(',')
      } else {
        processColorArr = new Array(processNameArr.length)
      }

      // 报工状态
      if (this.item.status !== null && this.item.status !== undefined && this.item.status !== '') {
        statusArr = this.item.status.split(',')
      } else {
        statusArr = new Array(processNameArr.length)
      }

      // 工序名称
      if (this.item.name !== null && this.item.name !== undefined && this.item.name !== '') {
        nameArr = this.item.name.split(',')
      } else {
        nameArr = new Array(processNameArr.length)
      }

      var item = {}
      var inStore = ''
      for (var i = 0; i < count; i++) {
        item = {}
        item.index = i
        item.processName = processNameArr[i]

        // 工序入库,高亮工序
        // 未入库，依然灰色
        inStore = ''
        inStore = i < inStoreStatusArr.length ? inStoreStatusArr[i] : ''

        var temp = i < processColorArr.length ? processColorArr[i] : ''
        if (temp === '' || temp === undefined) {
          temp = ''
        }
        // item.background = temp

        if (inStore !== '' && inStore !== undefined) {
          // item.inStore = 1
          item.background = this.rgbToRgba(temp, 1)
        } else {
          // 没入库的时候
          item.background = this.rgbToRgba(temp, 0.08)
        }

        // 报工结束
        item.status = i < statusArr.length ? statusArr[i] : ''
        if (item.status === '' || item.status === undefined) {
          item.status = ''
        }

        // 弃用报工,高亮颜色
        // 颜色
        // if (item.status === '工件完成' || item.status === '已结束') {
        //   item.background = 'green'
        // } else {
        //   item.background = 'orange'
        // }
        processArr.push(item)
      }
      return processArr
    },
    set16ToRgb(str) {
      var reg = /^#([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$/
      if (!reg.test(str)) {
        return
      }
      let newStr = str.toLowerCase().replace(/\#/g, '')
      let len = newStr.length
      if (len == 3) {
        let t = ''
        for (var i = 0; i < len; i++) {
          t += newStr.slice(i, i + 1).concat(newStr.slice(i, i + 1))
        }
        newStr = t
      }
      let arr = [] // 将字符串分隔，两个两个的分隔
      for (var i = 0; i < 6; i = i + 2) {
        let s = newStr.slice(i, i + 2)
        arr.push(parseInt('0x' + s))
      }
      return 'rgb(' + arr.join(',') + ')'
    },
    rgbToRgbaSub(color, alp) {
      var result = ''
      var rgbaAttr = color.match(/[\d.]+/g)
      if (rgbaAttr.length >= 3) {
        var r = rgbaAttr[0]
        var g = rgbaAttr[1]
        var b = rgbaAttr[2]
        result = 'rgba(' + r + ',' + g + ',' + b + ',' + alp + ')'
      }
      // console.log(result)
      return result
    },
    rgbToRgba(color, alp) {
      var result = ''
      try {
        var rgb = this.set16ToRgb(color)
        result = this.rgbToRgbaSub(rgb, alp)
      } catch (ex) {
        console.log(ex)
      }
      return result
    }
  }
}
</script>

<style>
.btnMask {
  filter: mask(color=#030a0356);
}
.inStore {
  background-color: hsl(0, 100%, 50%);
}
.unStore {
  background-color: hsl(0, 100%, 100%);
}
</style>
