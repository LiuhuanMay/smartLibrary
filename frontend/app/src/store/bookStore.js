import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useBookStore = defineStore('book', () => {
    const currentBook = ref(null)

    const setCurrentBook = (book) => {
        currentBook.value = book
    }

    return { currentBook, setCurrentBook }
})

