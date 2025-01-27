<script setup lang="ts">

import type { ResourceBank } from "@/ResourceBank.ts";

defineProps<{ bank:ResourceBank }>()
</script>

<template>
  {{JSON.stringify(bank)}}
</template>

<style scoped>

</style>
