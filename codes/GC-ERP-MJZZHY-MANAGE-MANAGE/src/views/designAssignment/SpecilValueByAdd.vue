<template>
  <div>
    <span v-if="add !=='' && add !==null && add !=0">{{ value }}{{ add }}</span>
    <span v-else>{{ value }}</span>
  </div>
</template>
<script>
/**
 * 2022-6-6
 * 浏览的是时候 长，宽，厚 要显示加量的字符串
 */
export default {
  name: 'SpecilValueByAddCom',
  props: {
    value: {
      type: Number,
      default: () => 0
    },
    add: {
      type: String,
      default: () => ''
    }
  },

  data() {
    return {
      mode: 1
    }
  }
}
</script>
