<script setup lang="ts">
import type { Card } from "@/game.ts";

defineProps<Card>()
</script>

<template>
  <div>
    <span v-text="name"/>
    <p v-text="`production : ${JSON.stringify(production)}`"/>
  </div>
</template>

<style scoped>

</style>
