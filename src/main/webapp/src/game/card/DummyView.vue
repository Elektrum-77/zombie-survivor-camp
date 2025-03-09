<script setup lang="ts">
import CardLayout from "@/game/card/CardLayout.vue";
import CardImageSkeleton from "@/game/card/CardImageSkeleton.vue";
import type { Dummy } from "@/game/card/type.ts";
import CardName from "@/game/card/CardName.vue";
defineProps<Dummy>()
</script>

<template>
<CardLayout skeleton>
  <CardName :name/>
  <CardImageSkeleton/>
</CardLayout>
</template>

<style scoped>

</style>
