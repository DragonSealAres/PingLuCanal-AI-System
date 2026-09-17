<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { createUser, getUsers, resetUserPassword, updateUser } from '../api/user'

const loading = ref(false)
const saving = ref(false)
const users = ref([])
const dialogVisible = ref(false)
const editingUser = ref(null)
const formRef = ref(null)
const form = reactive({
  username: '',
  realName: '',
  phone: '',
  role: 'INSPECTOR',
  status: 'ENABLED',
  password: '123456',
})

const roleOptions = [
  { label: '巡检员', value: 'INSPECTOR' },
  { label: '处置人员', value: 'WORKER' },
  { label: '管理员', value: 'ADMIN' },
  { label: '系统管理员', value: 'SUPER_ADMIN' },
]

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
}

function roleLabel(role) {
  return roleOptions.find((item) => item.value === role)?.label || role
}

function resetForm() {
  editingUser.value = null
  Object.assign(form, {
    username: '',
    realName: '',
    phone: '',
    role: 'INSPECTOR',
    status: 'ENABLED',
    password: '123456',
  })
}

async function loadUsers() {
  loading.value = true
  try {
    users.value = (await getUsers()) || []
  } finally {
    loading.value = false
  }
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  editingUser.value = row
  Object.assign(form, {
    username: row.username,
    realName: row.realName,
    phone: row.phone || '',
    role: row.role,
    status: row.status,
    password: '123456',
  })
  dialogVisible.value = true
}

async function submit() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (editingUser.value) {
      await updateUser(editingUser.value.id, {
        realName: form.realName,
        phone: form.phone,
        role: form.role,
        status: form.status,
      })
      ElMessage.success('用户已更新')
    } else {
      await createUser({ ...form })
      ElMessage.success('用户已创建')
    }
    dialogVisible.value = false
    await loadUsers()
  } finally {
    saving.value = false
  }
}

async function resetPassword(row) {
  const { value } = await ElMessageBox.prompt(`重置 ${row.username} 的密码`, '重置密码', {
    inputValue: '123456',
    inputPattern: /^.{6,50}$/,
    inputErrorMessage: '密码长度需为6-50位',
  })
  await resetUserPassword(row.id, value)
  ElMessage.success('密码已重置')
}

onMounted(loadUsers)
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <div>
        <h2>用户管理</h2>
        <p>管理演示账号、角色和启用状态</p>
      </div>
      <div class="toolbar-actions">
        <el-button @click="loadUsers">刷新</el-button>
        <el-button type="primary" @click="openCreate">新增用户</el-button>
      </div>
    </div>

    <section class="panel">
      <el-table v-loading="loading" :data="users" row-key="id" stripe height="calc(100vh - 238px)">
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column label="角色" width="140">
          <template #default="{ row }">{{ roleLabel(row.role) }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="180">
          <template #default="{ row }">{{ row.createTime?.replace('T', ' ').slice(0, 19) || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="resetPassword(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingUser ? '编辑用户' : '新增用户'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!editingUser" maxlength="50" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" maxlength="100" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" maxlength="30" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" style="width: 100%">
            <el-option v-for="item in roleOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="启用" value="ENABLED" />
            <el-option label="禁用" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!editingUser" label="初始密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.toolbar-actions {
  display: flex;
  gap: 10px;
}
</style>
