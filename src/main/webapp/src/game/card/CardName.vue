<script setup lang="ts">
defineProps<{name: string}>()
</script>

<template>
  <div class="card-name">
    <span v-text="name"/>
  </div>
</template>

<style scoped>
.card-name {
  height: fit-content;
  text-wrap: balance;
}

span {
  font-weight: bold;
}
</style>
