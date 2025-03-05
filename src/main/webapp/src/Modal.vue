<script setup lang="ts">
import { watch } from 'vue';
import { vOnClickOutside } from "@vueuse/components";

const {show} = defineProps<{ show: boolean; }>()
const emit = defineEmits<{
  opened: [],
  closed: [],
}>()

watch(() => show, (v) => v ? emit('opened') : emit("closed"))
</script>

<template>
  <Teleport to="body">
    <div v-if="show" class="modal-background">
      <div class="modal-content" v-on-click-outside="()=>show=false">
        <slot/>
      </div>
    </div>
    <slot
      name="state"
      :close="()=>show=false"
      :open="()=>show=true"
      :show="(v:boolean)=>show=v"
      :is-opened="show"
      :is-closed="!show"
    />
  </Teleport>
</template>

<style scoped>
.modal-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: transparent;
  width: fit-content;
  height: fit-content;
  position: relative;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    transform: scale(0.9);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
</style>
