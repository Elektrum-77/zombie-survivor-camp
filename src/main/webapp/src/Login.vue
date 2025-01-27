<script setup lang="ts">
const username = defineModel<string>("username", {required: true})

function connect(username: string): WebSocket {
  const url = `ws://${import.meta.env.VITE_SERVER}/game/${username}`
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
