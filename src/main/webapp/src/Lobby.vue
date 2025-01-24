<script setup lang="ts">

defineProps<{
  players: {
    username: string
    isReady: boolean
  }[]
}>()

defineEmits<{
  ready: [v: boolean]
}>()
</script>

<template>
  <div>
    <div>
      <p>Lobby</p>
      <ul>
        <li v-for="{username, isReady} in players">
          <p v-text="`username : ${username}`"/>
          <p v-text="`ready : ${isReady}`"/>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped>

</style>
