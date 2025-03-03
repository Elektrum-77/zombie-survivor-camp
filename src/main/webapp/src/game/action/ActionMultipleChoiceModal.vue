<script setup lang="ts">
import { VueFinalModal } from 'vue-final-modal'
import type { Action } from "@/game/action/Action.ts";

defineProps<{actions: Action[]}>()
defineEmits<{ selected: [Action] }>()
</script>

<template>
  <VueFinalModal
    class="confirm-modal"
    content-class="confirm-modal-content"
    overlay-transition="vfm-fade"
    content-transition="vfm-fade"
  >
    <ul>
      <li
        v-for="action in actions"
        v-text="JSON.stringify(action)"
        @click="$emit('selected', action)"
      />
    </ul>
  </VueFinalModal>
</template>

<style scoped>
.confirm-modal {
  display: flex;
  justify-content: center;
  align-items: center;
}
.confirm-modal-content {
  display: flex;
  flex-direction: column;
  padding: 1rem;
  background: #fff;
  border-radius: 0.5rem;
}

</style>
