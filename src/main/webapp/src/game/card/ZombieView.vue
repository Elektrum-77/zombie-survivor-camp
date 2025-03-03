<script setup lang="ts">
import type { Zombie } from "@/game/card/Card.ts";

defineProps<Zombie>()
</script>

<template>
  <div class="zombie-layout col">
    <div class="row">
      <span>Danger level : {{ count }}</span>
    </div>
  </div>
</template>

<style scoped>
.zombie-layout {
  gap: 0.5rem;
}

.zombie-layout > div {
  gap: 0;
}

</style>
