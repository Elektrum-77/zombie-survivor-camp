<script setup lang="ts" generic="T">

import { Icon } from "@iconify/vue";

defineProps<{
  actions: {
    value: T
    icon: string
    color?: string
  }[]
}>()
defineEmits<{selected: [T]}>()
</script>

<template>
<div class="row">
  <Icon
    v-for="{value, icon, color} in actions"
    :icon
    :color
    @click="$emit('selected', value)"
    width="2rem"
    height="2rem"
  />
</div>
</template>

<style scoped>
div {
  align-items: stretch;
  justify-content: center;
}

div > * {
  cursor: pointer;
  transition: all 0.25s;
}

div > *:hover {
  transform: translateY(-0.5rem);
}
</style>
