<script setup lang="ts">
import type { Card } from "@/card/Card.ts";
import { ref, watch } from "vue";
import ActionSelector from "@/ActionSelector.vue";
import type { Camp } from "@/game.ts";
import CardView from "@/card/CardView.vue";
import type { Action } from "@/Action.ts";
import { vOnClickOutside, vElementHover } from "@vueuse/components";

defineProps<{ camp: Camp, card: Card, index:number }>()
defineEmits<{action: [Action]}>()
const hovered = ref(false)
const clicked = ref(false)
</script>

<template>
<div class="col" v-element-hover="v=>hovered=v">
  <ActionSelector v-if="hovered||clicked" :camp :card :index @action="$emit('action', $event)" />
  <CardView v-bind="card" @click="clicked=true" v-on-click-outside="()=>clicked=false"/>
</div>
</template>

<style scoped>
</style>
