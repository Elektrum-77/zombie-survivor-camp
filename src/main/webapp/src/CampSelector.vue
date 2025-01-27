<script setup lang="ts">
defineProps<{camps: string[]}>()
defineModel<string>("camp", {required: true})
</script>

<template>
  <div class="row">
    <div
      v-for="c in camps"
      class="camp"
      :aria-selected="c === camp"
      @click="$emit('update:camp', c)"
    >
      <span>{{ c }}</span>
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
