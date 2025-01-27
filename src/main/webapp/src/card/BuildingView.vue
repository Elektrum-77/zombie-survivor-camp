<script setup lang="ts">
import type { Building } from "@/card/Card.ts";
import ResourceBankView from "@/ResourceBankView.vue";

defineProps<Building>()
</script>

<template>
  <div class="col">
    <span>cost : <ResourceBankView :bank="cost"/> </span>
    <span>production : <ResourceBankView :bank="production"/> </span>
    <span>search : <ResourceBankView :bank="search"/> </span>
  </div>
</template>

<style scoped>

</style>
