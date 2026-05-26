import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.tsx'
import '../styles/index.css'

declare global {
	interface Window {
		__reactChildrenOnlyPatched__?: boolean
	}
}

function patchReactChildrenOnly() {
	if (typeof window === 'undefined' || window.__reactChildrenOnlyPatched__) {
		return
	}

	const reactWithMutableChildren = React as typeof React & {
		Children: typeof React.Children & {
			only: typeof React.Children.only
		}
	}
	const originalOnly = reactWithMutableChildren.Children.only

	reactWithMutableChildren.Children.only = ((children: React.ReactNode) => {
		if (React.isValidElement(children)) {
			return originalOnly(children)
		}

		if (children == null) {
			return null as never
		}

		const childArray = React.Children.toArray(children)
		const firstElement = childArray.find((child) => React.isValidElement(child))

		if (firstElement && React.isValidElement(firstElement)) {
			return firstElement
		}

		return null as never
	}) as typeof React.Children.only

	window.__reactChildrenOnlyPatched__ = true
}

patchReactChildrenOnly()

ReactDOM.createRoot(document.getElementById('root')!).render(
	<React.StrictMode>
		<App />
	</React.StrictMode>,
)