<script setup lang="ts">
import CardLayout from "@/game/card/CardLayout.vue";
import CardName from "@/game/card/CardName.vue";
import { Icon } from "@iconify/vue";

defineProps<{
  name: string
  icon: string
}>()
</script>

<template>
  <CardLayout skeleton class="card-with-icon">
    <CardName :name/>
    <Icon :icon width="50%" height="50%" style="margin: auto"/>
  </CardLayout>
</template>

<style scoped>
.card-with-icon {
  display: grid;
  grid-template-rows: auto 1fr;
}
</style>
