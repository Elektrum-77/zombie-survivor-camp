<script setup lang="ts">
import type { Card } from "@/card/Card.ts";
import CardView from "@/card/CardView.vue";
import { Icon } from "@iconify/vue";
import { ref } from "vue";
import { vElementHover, vOnClickOutside } from "@vueuse/components";

defineProps<{ card: Card }>()
defineEmits<{ removed: [] }>()
const clicked = ref<boolean>(false)
const hovered = ref<boolean>(false)
const icon = "mdi:trash"
const iconClicked = "mdi:trash-can-empty"
</script>

<template>
  <div
    class="card"
    @click="clicked=true"
    v-on-click-outside="()=>clicked=false"
    v-element-hover="(v)=>hovered=v"
  >
    <CardView :card/>
    <div v-show="clicked || hovered" class="remove-icon" :clicked="clicked">
      <Icon
        :icon="clicked ? iconClicked : icon"
        width="3rem"
        height="3rem"
        :color="clicked?'firebrick':undefined"
      />
    </div>
  </div>
</template>

<style scoped>
.card {
  position: relative;
}

.remove-icon {
  display: none;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.remove-icon[clicked=true] {
  background-color: rgb(255, 127, 80, 0.25);
}

.card:hover .remove-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
