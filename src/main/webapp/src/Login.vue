<script setup lang="ts">
import { useBrowserLocation } from "@vueuse/core";

const username = defineModel<string>("username", {required: true})

const location = useBrowserLocation()

function connect(username: string): WebSocket {
  // const url = `ws://${location.value.host}/game/${username}`
  // const url = `ws://88.124.177.238:49152/game/${username}`
  const url = `ws://localhost:49152/game/${username}`
  console.log(`Connecting to ${url}`)
  return new WebSocket(url)
}

defineEmits<{
  connected: [socket: WebSocket]
}>()
</script>

<template>
  <div>
    <p>Username : <input type="text" v-model="username"/></p>
    <button @click="$emit('connected', connect(username))">Connect!</button>
  </div>
</template>

<style scoped>

</style>
