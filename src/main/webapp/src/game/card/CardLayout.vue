<script setup lang="ts">
defineProps<{ skeleton?: boolean }>()
</script>

<template>
  <div class="card-layout card-size" :skeleton>
    <slot/>
  </div>
</template>

<style scoped>
.card-layout {
  display: flex;
  flex-direction: column;
  background-color: white;
  border: 0.2rem solid var(--vt-c-indigo);
  border-radius: 0.8rem;
}

.card-layout[skeleton=true] {
  background-color: transparent;
  border-style: dashed;
}

.card-layout > * {
  margin: 0 0.5rem 0 0.5rem;
}
</style>
