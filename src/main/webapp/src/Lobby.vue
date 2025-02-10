<script setup lang="ts">
import { shallowRef, watch } from "vue";

const {players} = defineProps<{
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
  <div>
    <div class="col" style="align-items: center">
      <h2>Lobby</h2>
      <div class="row player-list">
        <div v-for="(isReady, username) in players" class="col player">
          <span class="username">{{username}}</span>
          <span :ready="isReady">{{readyLabel(isReady)}}</span>
        </div>
      </div>
      <button @click="ready = !ready">{{readyLabel(!ready)}}</button>
    </div>
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
