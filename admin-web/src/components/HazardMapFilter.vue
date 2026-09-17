<script setup>
const props = defineProps({
  modelValue: {
    type: Object,
    required: true,
  },
  summary: {
    type: Object,
    required: true,
  },
})

const emit = defineEmits(['update:modelValue', 'reset'])

const riskOptions = ['高风险', '中风险', '低风险']
const statusOptions = ['待审核', '已审核', '处理中', '待复核', '已完成']
const hazardTypeOptions = ['漂浮物', '航道障碍物', '船舶异常', '航标异常', '水面污染', '岸线异常', '非法占道', '其他']

function updateField(key, value) {
  emit('update:modelValue', {
    ...props.modelValue,
    [key]: value,
  })
}
</script>

<template>
  <section class="panel map-filter-panel">
    <el-form :inline="true" :model="props.modelValue" class="map-filters">
      <el-form-item label="风险等级">
        <el-select
          :model-value="props.modelValue.riskLevel"
          clearable
          placeholder="全部"
          style="width: 140px"
          @update:model-value="updateField('riskLevel', $event)"
        >
          <el-option v-for="item in riskOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="隐患状态">
        <el-select
          :model-value="props.modelValue.status"
          clearable
          placeholder="全部"
          style="width: 140px"
          @update:model-value="updateField('status', $event)"
        >
          <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="隐患类型">
        <el-select
          :model-value="props.modelValue.hazardType"
          clearable
          placeholder="全部"
          style="width: 160px"
          @update:model-value="updateField('hazardType', $event)"
        >
          <el-option v-for="item in hazardTypeOptions" :key="item" :label="item" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="emit('reset')">重置</el-button>
      </el-form-item>
    </el-form>
    <div class="filter-summary">
      当前筛选 {{ props.summary.total }} 条，地图显示 {{ props.summary.visible }} 个点位
    </div>
  </section>
</template>

<style scoped>
.map-filter-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px 0;
}

.map-filters {
  flex: 1;
}

.filter-summary {
  padding-bottom: 16px;
  color: #627d98;
  white-space: nowrap;
}
</style>
