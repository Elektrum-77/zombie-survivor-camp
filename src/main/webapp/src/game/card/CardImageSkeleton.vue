<script setup lang="ts">
import CardImageLayout from "@/game/card/CardImageLayout.vue";
import { Icon } from "@iconify/vue"
</script>

<template>
  <CardImageLayout>
    <div class="image-skeleton hor ver">
        <Icon icon="mdi:image" width="3rem" height="3rem"/>
    </div>
  </CardImageLayout>
</template>

<style scoped>

.image-skeleton {
  /* does not work :( needs at least width or height but either both are 100% or both are unknown
    aspect-ratio: 512/341;
    object-fit: contain;
  */
  width: 100%;
  height: 100%;
  border: 0.2rem dashed var(--vt-c-indigo);
  border-radius: 0.8rem;
}
</style>
