<script setup>
import AMapLoader from '@amap/amap-jsapi-loader'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import HazardMapFilter from '../components/HazardMapFilter.vue'
import HazardMapInfo from '../components/HazardMapInfo.vue'
import { getHazards } from '../api/hazard'

const router = useRouter()
const mapLoading = ref(false)
const dataLoading = ref(false)
const mapError = ref('')
const dataError = ref('')
const markerError = ref('')
const hazards = ref([])
const selectedHazard = ref(null)
const filters = ref({
  riskLevel: '',
  status: '',
  hazardType: '',
})

let AMapInstance = null
let map = null
let markers = []

const defaultCenter = [108.65, 22.45]

const filteredHazards = computed(() => hazards.value.filter((item) => {
  return (!filters.value.riskLevel || item.riskLevel === filters.value.riskLevel)
    && (!filters.value.status || item.status === filters.value.status)
    && (!filters.value.hazardType || item.hazardType === filters.value.hazardType)
}))

const mappableHazards = computed(() => filteredHazards.value.filter(hasValidCoordinate))
const missingLocationCount = computed(() => filteredHazards.value.length - mappableHazards.value.length)
const filteredStats = computed(() => ({
  total: filteredHazards.value.length,
  highRisk: filteredHazards.value.filter((item) => item.riskLevel === '高风险').length,
  processing: filteredHazards.value.filter((item) => item.status === '处理中').length,
  done: filteredHazards.value.filter((item) => item.status === '已完成').length,
}))
const filterSummary = computed(() => ({
  total: filteredHazards.value.length,
  visible: mappableHazards.value.length,
}))

function coordinateOf(item) {
  return [Number(item.longitude), Number(item.latitude)]
}

function hasValidCoordinate(item) {
  if (item.longitude === null || item.longitude === undefined || item.latitude === null || item.latitude === undefined) {
    return false
  }

  const [lng, lat] = coordinateOf(item)
  return Number.isFinite(lng)
    && Number.isFinite(lat)
    && lng >= -180
    && lng <= 180
    && lat >= -90
    && lat <= 90
}

function markerClass(level) {
  if (level === '高风险') return 'danger'
  if (level === '中风险') return 'warning'
  if (level === '低风险') return 'success'
  return 'info'
}

function markerLabel(level) {
  if (level === '高风险') return '高'
  if (level === '中风险') return '中'
  if (level === '低风险') return '低'
  return '隐'
}

function resolveAmapConfig() {
  const key = import.meta.env.VITE_AMAP_KEY
  const securityJsCode = import.meta.env.VITE_AMAP_SECURITY_JS_CODE

  if (!key || !securityJsCode) {
    throw new Error('未配置高德地图Key')
  }

  return { key, securityJsCode }
}

function resolveAmapError(error) {
  const message = error?.message || String(error || '')
  if (message.includes('INVALID_USER_KEY')) {
    return '高德地图Key无效，请检查 VITE_AMAP_KEY 是否填写正确。'
  }
  if (message.includes('USERKEY_PLAT_NOMATCH')) {
    return '高德地图Key平台类型不匹配，请确认已申请 Web端(JS API) Key。'
  }
  if (message.includes('安全密钥') || message.includes('INVALID_SECURITY_CODE')) {
    return '高德地图安全密钥错误，请检查 VITE_AMAP_SECURITY_JS_CODE。'
  }
  return message || '高德地图加载失败'
}

async function initMap() {
  if (map) return

  mapLoading.value = true
  mapError.value = ''

  try {
    const { key, securityJsCode } = resolveAmapConfig()
    window._AMapSecurityConfig = {
      securityJsCode,
    }

    AMapInstance = await AMapLoader.load({
      key,
      version: '2.0',
      plugins: [],
    })

    await nextTick()
    map = new AMapInstance.Map('map-container', {
      center: defaultCenter,
      zoom: 11,
      viewMode: '2D',
      resizeEnable: true,
    })
    renderMarkers()
  } catch (error) {
    mapError.value = resolveAmapError(error)
  } finally {
    mapLoading.value = false
  }
}

async function loadHazards() {
  dataLoading.value = true
  dataError.value = ''

  try {
    hazards.value = (await getHazards()) || []
    syncSelectedHazard()
    renderMarkers()
  } catch (error) {
    dataError.value = error?.message || '隐患列表接口加载失败'
  } finally {
    dataLoading.value = false
  }
}

function resetFilters() {
  filters.value = {
    riskLevel: '',
    status: '',
    hazardType: '',
  }
}

function clearMarkers() {
  markers.forEach((marker) => {
    try {
      marker.setMap(null)
    } catch {
      // Ignore stale marker cleanup failures from the map SDK.
    }
  })
  markers = []
}

function markerContent(item) {
  const level = item.riskLevel || ''
  return `<div class="amap-risk-marker amap-risk-${markerClass(level)}"><span>${markerLabel(level)}</span></div>`
}

function createMarker(item) {
  const marker = new AMapInstance.Marker({
    position: coordinateOf(item),
    offset: new AMapInstance.Pixel(-17, -34),
    content: markerContent(item),
    title: `${item.reportNo || ''} ${item.hazardType || ''} ${item.riskLevel || ''}`,
  })

  marker.__hazard = {
    id: item.id,
    reportNo: item.reportNo,
    hazardType: item.hazardType,
    riskLevel: item.riskLevel,
    status: item.status,
    location: item.location,
    imageUrl: item.imageUrl,
    description: item.description,
    createTime: item.createTime,
  }
  marker.on('click', () => {
    selectedHazard.value = item
    map.setCenter(coordinateOf(item))
  })

  return marker
}

function renderMarkers() {
  if (!map || !AMapInstance) return

  markerError.value = ''
  clearMarkers()

  try {
    markers = mappableHazards.value.map(createMarker)
    markers.forEach((marker) => marker.setMap(map))
    updateMapView()
  } catch (error) {
    clearMarkers()
    markerError.value = error?.message || 'Marker生成失败'
  }
}

function updateMapView() {
  if (!map) return

  if (markers.length > 1) {
    map.setFitView(markers, false, [70, 70, 70, 70])
    return
  }

  if (markers.length === 1) {
    map.setCenter(markers[0].getPosition())
    map.setZoom(14)
    return
  }

  map.setCenter(defaultCenter)
  map.setZoom(11)
}

function syncSelectedHazard() {
  if (selectedHazard.value && filteredHazards.value.some((item) => item.id === selectedHazard.value.id)) {
    return
  }
  selectedHazard.value = mappableHazards.value[0] || null
}

function handleViewDetail(hazard) {
  if (hazard?.id) {
    router.push(`/hazards/${hazard.id}`)
  }
}

watch(filteredHazards, () => {
  syncSelectedHazard()
  renderMarkers()
})

onMounted(async () => {
  await Promise.all([loadHazards(), initMap()])
})

onBeforeUnmount(() => {
  clearMarkers()
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<template>
  <div class="page hazard-map-page">
    <div class="toolbar">
      <div>
        <h2>航道隐患一张图</h2>
        <p>基于数据库隐患经纬度展示航道风险点位</p>
      </div>
      <div class="toolbar-actions">
        <el-button :loading="dataLoading" @click="loadHazards">刷新隐患</el-button>
        <el-button type="primary" @click="router.push('/dashboard')">返回Dashboard</el-button>
      </div>
    </div>

    <div class="metric-grid map-metrics">
      <section class="metric-card">
        <span>当前隐患总数</span>
        <strong>{{ filteredStats.total }}</strong>
      </section>
      <section class="metric-card danger">
        <span>高风险数量</span>
        <strong>{{ filteredStats.highRisk }}</strong>
      </section>
      <section class="metric-card warning">
        <span>处理中数量</span>
        <strong>{{ filteredStats.processing }}</strong>
      </section>
      <section class="metric-card success">
        <span>已完成数量</span>
        <strong>{{ filteredStats.done }}</strong>
      </section>
    </div>

    <HazardMapFilter
      v-model="filters"
      :summary="filterSummary"
      @reset="resetFilters"
    />

    <el-alert
      v-if="missingLocationCount > 0"
      :title="`有 ${missingLocationCount} 条隐患缺少位置信息，未显示在地图中`"
      type="warning"
      show-icon
      :closable="false"
    />
    <el-alert
      v-if="!dataLoading && !mapError && mappableHazards.length === 0"
      title="当前筛选条件下暂无隐患点"
      type="info"
      show-icon
      :closable="false"
    />
    <el-alert v-if="dataError" :title="dataError" type="error" show-icon :closable="false" />
    <el-alert v-if="mapError" :title="mapError" type="error" show-icon :closable="false" />
    <el-alert v-if="markerError" :title="markerError" type="error" show-icon :closable="false" />

    <section class="map-workspace">
      <div class="panel map-panel" v-loading="mapLoading || dataLoading">
        <div id="map-container"></div>
      </div>

      <HazardMapInfo :hazard="selectedHazard" @view-detail="handleViewDetail" />
    </section>
  </div>
</template>

<style scoped>
.toolbar-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.map-metrics {
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.map-workspace {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 16px;
  min-height: 600px;
}

.map-panel {
  overflow: hidden;
}

#map-container {
  width: 100%;
  height: 600px;
}

:global(.amap-risk-marker) {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  border: 3px solid #ffffff;
  border-radius: 50%;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.28);
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
}

:global(.amap-risk-danger) {
  background: #d1495b;
}

:global(.amap-risk-warning) {
  background: #e0a458;
}

:global(.amap-risk-success) {
  background: #2f855a;
}

:global(.amap-risk-info) {
  background: #718096;
}

@media (max-width: 1280px) {
  .map-workspace {
    grid-template-columns: minmax(0, 1fr) 320px;
  }
}
</style>
