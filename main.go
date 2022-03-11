package main

import (
	"net/http"
	"os"

	"github.com/labstack/echo/v4"
)

func main() {

	e := echo.New()

	e.GET("/", func(c echo.Context) error {
		return c.HTML(http.StatusOK, "Hello, Welcome to Aqua Secuirty.. !!")
	})

	e.GET("/ping", func(c echo.Context) error {
		return c.JSON(http.StatusOK, struct{ Status string }{Status: "OK"})
	})

	httpPort := os.Getenv("HTTP_PORT")
	if httpPort == "" {
		httpPort = "8080"
	}

	e.Logger.Fatal(e.Start(":" + httpPort))
}

