import { Component, ReactComponentElement } from "react"

export type MenuItemRoute = {
    name: string
    path: string
    componentFullName: string
    component?: ReactComponentElement
    params?: any
    routes?: MenuItemRoute[]
}

export type RouteComponentProps = {
    routes: MenuItemRoute[]
}