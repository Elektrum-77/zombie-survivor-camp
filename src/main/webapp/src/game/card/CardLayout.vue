<script setup lang="ts">
defineProps<{ skeleton?: boolean }>()
</script>

<template>
  <div class="padded card-layout bordered rounded card-size" :class="skeleton?'skeleton':undefined">
    <slot/>
  </div>
</template>

<style scoped>
.card-layout {
  background-color: white;
}

.skeleton {
  background-color: transparent;
  border-style: dashed;
}
</style>
