<script setup lang="ts">
import { CARD_IMG, isCardImage } from "@/game/card/asset.ts";
import { computed } from "vue";
import CardImageSkeleton from "@/game/card/CardImageSkeleton.vue";
import CardImageLayout from "@/game/card/CardImageLayout.vue";

const {name} = defineProps<{
  name: string
  skeleton?: boolean
}>()
const image = computed<string>(() => name.toLowerCase().replaceAll(' ', '_'))
</script>

<template>
  <CardImageLayout v-if="isCardImage(image) && !skeleton">
    <img :src="CARD_IMG[image]" class="image" :alt="image">
  </CardImageLayout>
  <CardImageSkeleton v-else/>
</template>

<style scoped>
.image {
  object-fit: contain;
  max-height: 100%;
  max-width: 100%;
  border-radius: 0.8rem;
}
</style>
