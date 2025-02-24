<script setup lang="ts">
defineProps<{usernames: string[]}>()
defineModel<string>("selected", {required: true})
</script>

<template>
  <div class="row">
    <div
      v-for="username in usernames"
      class="camp"
      :aria-selected="username === selected"
      @click="$emit('update:selected', username)"
    >
      <span>{{ username }}</span>
    </div>
  </div>
</template>

<style scoped>
.camp {
  border: 1px solid black;
  cursor: pointer;
  padding: 8px;
}

.camp[aria-selected=true] span {
  font-weight: bold;
}
</style>
