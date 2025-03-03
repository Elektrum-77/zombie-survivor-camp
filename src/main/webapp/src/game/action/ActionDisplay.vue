<script setup lang="ts">
import { computed, ref } from "vue";
import type { Action, ActionType } from "@/game/action/Action.ts";
import { ICON_ACTION } from "@/icon.ts";
import { Icon } from "@iconify/vue";
import ActionMultipleChoiceModal from "@/game/action/ActionMultipleChoiceModal.vue";
import { useVfm } from 'vue-final-modal'

const vfm = useVfm()
const {actions} = defineProps<{ actions: Action[] }>()
const emit = defineEmits<{ selected: [Action] }>()
const actionByType = computed(() => {
  const result = {} as Record<ActionType, Action[]>
  actions.forEach(action => {
    result[action.type] = result[action.type] || []
    result[action.type].push(action)
  })
  return result
})

const modalId = "actionModal"
const multipleChoiceActions = ref<Action[]>([])

function openMultipleChoiceModal(actions: Action[]) {
  multipleChoiceActions.value = actions
  vfm.open(modalId)
}

function select(action: Action) {
  emit("selected", action)
  vfm.close(modalId)
}
</script>

<template>
  <div class="row">
    <Icon
      v-for="(list, type) in actionByType"
      :icon="ICON_ACTION[type]"
      :color="type === 'DestroyBuilding' || type === 'CancelSearch' ? 'red' : undefined"
      @click="() => list.length > 1 ? openMultipleChoiceModal(list) : $emit('selected', list[0])"
      width="2rem"
      height="2rem"
    />
    <ActionMultipleChoiceModal
      :modalId
      :actions="multipleChoiceActions"
      @selected="select($event)"
    />
  </div>
</template>

<style scoped>
div {
  align-items: stretch;
  justify-content: center;
}

div > * {
  cursor: pointer;
  transition: all 0.25s;
}

div > *:hover {
  transform: translateY(-0.5rem);
}
</style>
