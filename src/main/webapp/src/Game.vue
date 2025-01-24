<script setup lang="ts">
import type { GameState } from "@/game.ts";
import Card from "@/assets/Card.vue";

defineProps<GameState & {username:string}>()
</script>

<template>
<div>
  <div>
    <p>Camp : </p>
    <p v-text="`production : ${JSON.stringify(camps[username])}`"/>
  </div>
  <div>
    <p>Hand : </p>
    <ul>
      <li v-for="{name, production} in hand">
        <Card :name="name" :production="production"/>
      </li>
    </ul>
  </div>
</div>
</template>

<style scoped>

</style>
