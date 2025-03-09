<script setup lang="ts">
import type { Zombie } from "@/game/card/type.ts";
import CardLayout from "@/game/card/CardLayout.vue";
import CardImage from "@/game/card/CardImage.vue";
import ResourceView from "@/game/ResourceView.vue";
import CardName from "@/game/card/CardName.vue";

defineProps<Zombie>()
</script>

<template>
  <CardLayout>

    <div class="row" style="justify-content: space-between">
      <CardName :name/>
      <ResourceView resource="PEOPLE" :count color="darkorchid"/>
    </div>
    <CardImage :name/>
    <!--  Spacer  -->
    <div style="height: 0.5rem"/>
  </CardLayout>
</template>

<style scoped>

</style>
