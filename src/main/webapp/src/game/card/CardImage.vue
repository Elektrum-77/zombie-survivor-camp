<script setup lang="ts">
import { CARD_IMG, isCardImage } from "@/game/card/asset.ts";
import { computed } from "vue";
import CardImageSkeleton from "@/game/card/CardImageSkeleton.vue";

const {name} = defineProps<{
  name: string
}>()
const image = computed<string>(() => name.toLowerCase().replaceAll(' ', '_'))
</script>

<template>
  <div v-if="isCardImage(image)" class="center-content">
    <img :src="CARD_IMG[image]" class="image rounded" :alt="image">
  </div>
  <CardImageSkeleton v-else/>
</template>

<style scoped>
.image {
  object-fit: contain;
  max-height: 100%;
  max-width: 100%;
}
</style>
