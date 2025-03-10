<script setup lang="ts">
import { shallowRef, watch } from "vue";

defineProps<{
  players: Record<string, boolean>
}>()

const emit = defineEmits<{
  ready: [v: boolean]
}>()

const ready = shallowRef(false)

function readyLabel(v: boolean) {
  return v ? "READY" : "NOT READY"
}

watch(() => ready.value, isReady => emit("ready", isReady))
</script>

<template>
  <div class="col center-items center-content gap">
    <h1>Lobby</h1>
    <div class="row player-list">
      <div v-for="(isReady, username) in players" class="col player">
        <span class="username" v-text="username"/>
        <span :ready="isReady" v-text="readyLabel(isReady)"/>
      </div>
    </div>
    <button
      class="clickable bordered padded rounded"
      @click="ready = !ready"
      v-text="readyLabel(!ready)"
    />
  </div>
</template>

<style scoped>
ul li {
  list-style: none;
}

.player-list {
  justify-content: center;
}

.player {
  align-items: center;
  border: 1px solid black;
  padding: 8px;
}

.player [ready=true] {
  color: green;
}

.player [ready=false] {
  color: red;
}

.username {
  font-weight: bold;
}
</style>
