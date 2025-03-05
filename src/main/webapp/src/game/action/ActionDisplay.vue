<script setup lang="ts">
import { computed, shallowRef } from "vue";
import type { Action, ActionType } from "@/game/action/Action.ts";
import { ICON_ACTION } from "@/assets/icon.ts";
import { Icon } from "@iconify/vue";
import Modal from "@/Modal.vue";

const {actions} = defineProps<{ actions: Action[] }>()
defineEmits<{ selected: [Action] }>()
const actionByType = computed(() => {
  const result = {} as Record<ActionType, Action[]>
  actions.forEach(action => {
    result[action.type] = result[action.type] || []
    result[action.type].push(action)
  })
  return result
})

const multipleChoiceActions = shallowRef<Action[]>([])
</script>

<template>
  <div class="row action-types">
    <Icon
      v-for="(list, type) in actionByType"
      :icon="ICON_ACTION[type]"
      :color="type === 'DestroyBuilding' || type === 'CancelSearch' ? 'red' : undefined"
      @click="() => list.length > 1 ? multipleChoiceActions = list : $emit('selected', list[0])"
      width="2rem"
      height="2rem"
    />
    <Modal :show="multipleChoiceActions.length > 1">
      <ul class="action-list">
        <li
          v-for="(action, i) in multipleChoiceActions" :key="i"
          v-text="JSON.stringify(action)"
          @click="() => {
            $emit('selected', action)
            multipleChoiceActions = []
          }"
        />
      </ul>
    </Modal>
  </div>
</template>

<style scoped>
.action-types {
  align-items: stretch;
  justify-content: center;
}

.action-types > * {
  cursor: pointer;
  transition: all 0.25s;
}

.action-types > *:hover {
  transform: translateY(-0.5rem);
}

.action-list {
  background-color: white;
  padding: 1rem;
  border-radius: 1rem;
  list-style: none;
}

.action-list > li:hover {
  transform: translateX(0.5rem) scale(1.05);
}
</style>
